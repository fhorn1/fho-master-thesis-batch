package org.fho.master.thesis.batch.ops;

import lombok.SneakyThrows;
import org.fho.master.thesis.batch.domain.OpsCode;
import org.fho.master.thesis.batch.domain.OpsHead;
import org.fho.master.thesis.batch.domain.model.generated.EhdHeaderTyp;
import org.fho.master.thesis.batch.domain.model.generated.Opscode;
import org.fho.master.thesis.batch.job.JobCompletionListener;
import org.fho.master.thesis.batch.ops.processor.OpsCodeItemProcessor;
import org.fho.master.thesis.batch.ops.processor.OpsHeadItemProcessor;
import org.fho.master.thesis.batch.ops.writer.OpsCodeItemWriter;
import org.fho.master.thesis.batch.ops.writer.OpsHeadItemWriter;
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
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.batch.item.xml.builder.StaxEventItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.nativex.hint.TypeAccess;
import org.springframework.nativex.hint.TypeHint;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import javax.sql.DataSource;
import javax.xml.bind.JAXBElement;
import java.nio.charset.StandardCharsets;

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
    public Step opsHeadImportStep(StaxEventItemReader<JAXBElement<EhdHeaderTyp>> opsHeadItemReader,
                                  ItemProcessor<JAXBElement<EhdHeaderTyp>, OpsHead> opsHeadItemProcessor,
                                  ItemWriter<OpsHead> opsHeadItemWriter) {
        return this.stepBuilderFactory.get("opsHeadImportStep")
                .<JAXBElement<EhdHeaderTyp>, OpsHead>chunk(10)
                .reader(opsHeadItemReader)
                .processor(opsHeadItemProcessor)
                .writer(opsHeadItemWriter)
                .listener(promotionListener())
                .build();
    }

    @Bean(name = "opsCodeImportStep") //name needed for spring-native with Qualifier above + Injection of Reader / Writer .. only works like this, not via bean method call
    public Step opsCodeImportStep(ItemReader<Opscode> opsCodeItemReader,
                            ItemProcessor<Opscode, OpsCode> opsCodeItemProcessor,
                            ItemWriter<OpsCode> opsCodeItemWriter) {
        return this.stepBuilderFactory.get("opsCodeImportStep")
                .<Opscode, OpsCode>chunk(500)
                .reader(opsCodeItemReader)
                .processor(opsCodeItemProcessor)
                .writer(opsCodeItemWriter)
                .build();
    }

    @Bean
    @SneakyThrows
    public StaxEventItemReader<JAXBElement<EhdHeaderTyp>> opsHeadItemReader() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setClassesToBeBound(EhdHeaderTyp.class);

        return new StaxEventItemReaderBuilder<JAXBElement<EhdHeaderTyp>>()
                .name("ops-head-reader")
                .encoding(StandardCharsets.ISO_8859_1.name())
                .resource(new ClassPathResource("catalogdata/SDOPS_01.00_74_tf+2022q1_nr+2.xml"))
                .addFragmentRootElements("header")
                .unmarshaller(marshaller)
                .build();
    }

    @Bean
    @SneakyThrows
    public StaxEventItemReader<Opscode> opsCodeItemReader() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setClassesToBeBound(Opscode.class);

        return new StaxEventItemReaderBuilder<Opscode>()
                .name("ops-reader")
                .encoding(StandardCharsets.ISO_8859_1.name())
                .resource(new ClassPathResource("catalogdata/SDOPS_01.00_74_tf+2022q1_nr+2.xml"))
                .addFragmentRootElements("opscode")
                .unmarshaller(marshaller)
                .build();
    }

    @Bean
    @StepScope
    public ItemProcessor<JAXBElement<EhdHeaderTyp>, OpsHead> opsHeadItemProcessor() {
        return new OpsHeadItemProcessor();
    }

    @Bean
    @StepScope
    public ItemProcessor<Opscode, OpsCode> opsCodeItemProcessor() {
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
    public ItemWriter<OpsCode> opsCodeItemWriter(DataSource dataSource) {
        final String sql = """
                INSERT INTO public.sdops_code(
                id, head_id, op_code, description, start_date, end_date, medical_motivation, side_location, category, category_info)
                VALUES  (:id, :headId, :opCode, :description, :startDate, :endDate, :medicalMotivation, :sideLocation, :category, :categoryInfo)""";
        return new OpsCodeItemWriter(dataSource, sql);
    }

    @Bean(name = "promotionListener")
    public ExecutionContextPromotionListener promotionListener() {
        ExecutionContextPromotionListener listener = new ExecutionContextPromotionListener();
        listener.setKeys(new String[]{"headId"});
        return listener;
    }

}