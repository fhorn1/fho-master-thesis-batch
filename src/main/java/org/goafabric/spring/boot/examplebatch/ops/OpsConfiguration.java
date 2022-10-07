package org.goafabric.spring.boot.examplebatch.ops;

import lombok.SneakyThrows;
import org.goafabric.spring.boot.examplebatch.domain.OpsCode;
import org.goafabric.spring.boot.examplebatch.domain.OpsHead;
import org.goafabric.spring.boot.examplebatch.domain.model.OpsImportedTuple;
import org.goafabric.spring.boot.examplebatch.job.JobCompletionListener;
import org.goafabric.spring.boot.examplebatch.ops.writer.ListUnpackingOpsCodeItemWriter;
import org.goafabric.spring.boot.examplebatch.ops.writer.OpsCodeItemWriter;
import org.goafabric.spring.boot.examplebatch.ops.writer.OpsHeadItemWriter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.listener.ExecutionContextPromotionListener;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.nativex.hint.TypeAccess;
import org.springframework.nativex.hint.TypeHint;

import javax.sql.DataSource;
import java.util.List;

@TypeHint(types = {OpsHead.class, OpsCode.class}, access = { TypeAccess.DECLARED_CONSTRUCTORS, TypeAccess.PUBLIC_METHODS })
@Configuration(proxyBeanMethods = false) //needed for spring-native
public class OpsConfiguration {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job opsCatalogImportJob(@Qualifier("opsHeadImportStep") Step opsHeadImportStep,
                                   @Qualifier("opsCodeImportStep") Step opsCodeImportStep,
                                   JobCompletionListener listener) {
        return jobBuilderFactory.get("opsCatalogImportJob")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .start(opsHeadImportStep)
                .next(opsCodeImportStep)
                .build();
    }

    @Bean(name = "opsHeadImportStep") //name needed for spring-native with Qualifier above + Injection of Reader / Writer .. only works like this, not via bean method call
    public Step opsHeadImportStep(ItemReader<OpsImportedTuple> opsHeadItemReader,
                                  ItemProcessor<OpsImportedTuple, OpsHead> opsHeadItemProcessor,
                                  ItemWriter<OpsHead> opsHeadItemWriter) {
        return this.stepBuilderFactory.get("opsHeadImportStep")
                .<OpsImportedTuple, OpsHead>chunk(10)
                .reader(opsHeadItemReader)
                .processor(opsHeadItemProcessor)
                .writer(opsHeadItemWriter)
                .listener(promotionListener())
                .build();
    }

    @Bean(name = "opsCodeImportStep") //name needed for spring-native with Qualifier above + Injection of Reader / Writer .. only works like this, not via bean method call
    public Step opsCodeImportStep(ItemReader<OpsImportedTuple> opsCodeItemReader,
                            ItemProcessor<OpsImportedTuple, List<OpsCode>> opsCodeItemProcessor,
                            ListUnpackingOpsCodeItemWriter<OpsCode> opsCodeItemWriter) {
        return this.stepBuilderFactory.get("opsCodeImportStep")
                .<OpsImportedTuple, List<OpsCode>>chunk(50)
                .reader(opsCodeItemReader)
                .processor(opsCodeItemProcessor)
                .writer(opsCodeItemWriter)
                .build();
    }

    @Bean
    @SneakyThrows
    public ItemReader<OpsImportedTuple> opsHeadItemReader() {
        return new OpsHeadItemReader(new ClassPathResource("catalogdata/SDOPS_01.00_74_tf+2022q1_nr+2.xml"));
    }

    @Bean
    @SneakyThrows
    public ItemReader<OpsImportedTuple> opsCodeItemReader() {
        return new OpsCodeItemReader(new ClassPathResource("catalogdata/SDOPS_01.00_74_tf+2022q1_nr+2.xml"));
    }

    @Bean
    @StepScope
    public ItemProcessor<OpsImportedTuple, OpsHead> opsHeadItemProcessor() {
        return new OpsHeadItemProcessor();
    }

    @Bean
    @StepScope
    public ItemProcessor<OpsImportedTuple, List<OpsCode>> opsCodeItemProcessor() {
        return new OpsCodeItemProcessor();
    }

    @Bean
    public JdbcBatchItemWriter<OpsHead> opsHeadItemWriter(DataSource dataSource) {
        final String sql = """
                INSERT INTO sdops_head(
                id, start_date, end_date, internal_version, kbv_version)
                VALUES (:id, :startDate, :endDate, :internalVersion, :kbvVersion)""";
        return new OpsHeadItemWriter(dataSource, sql);
    }

    @Bean
    public ListUnpackingOpsCodeItemWriter<OpsCode> opsCodeItemWriter(DataSource dataSource) {
        final String sql = """
                INSERT INTO public.sdops_code(
                id, head_id, op_code, description, start_date, end_date, medical_motivation, side_location, category, category_info)
                VALUES  (:id, :headId, :opCode, :description, :startDate, :endDate, :medicalMotivation, :sideLocation, :category, :categoryInfo)""";
        JdbcBatchItemWriter<OpsCode> opsCodeItemWriter = new OpsCodeItemWriter(dataSource, sql);

        ListUnpackingOpsCodeItemWriter<OpsCode> itemWriter = new ListUnpackingOpsCodeItemWriter<>();
        itemWriter.setDelegate(opsCodeItemWriter);
        return itemWriter;
    }

    @Bean(name = "promotionListener")
    public ExecutionContextPromotionListener promotionListener() {
        ExecutionContextPromotionListener listener = new ExecutionContextPromotionListener();
        listener.setKeys(new String[]{"headId"});
        return listener;
    }

}