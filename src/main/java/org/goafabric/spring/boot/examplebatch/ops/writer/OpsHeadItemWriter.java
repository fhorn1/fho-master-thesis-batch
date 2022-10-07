package org.goafabric.spring.boot.examplebatch.ops.writer;

import lombok.extern.slf4j.Slf4j;
import org.goafabric.spring.boot.examplebatch.domain.OpsHead;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;

import javax.sql.DataSource;
import java.util.List;

@Slf4j
public class OpsHeadItemWriter extends JdbcBatchItemWriter<OpsHead> {
    private final DataSource dataSource;

    private final String sql;

    private StepExecution stepExecution;

    public OpsHeadItemWriter(DataSource dataSource, String sql) {
        this.dataSource = dataSource;
        this.sql = sql;
    }

    @BeforeStep
    public void saveStepExecution(StepExecution stepExecution) {
        this.stepExecution = stepExecution;
    }

    @Override
    public void afterPropertiesSet() {
        this.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
        this.setDataSource(dataSource);
        this.setSql(sql);
        super.afterPropertiesSet();
    }

    public void write(List<? extends OpsHead> items) throws Exception {
        items.forEach(item -> log.info("Writing item: {}", item));

        ExecutionContext executionContext = this.stepExecution.getExecutionContext();
        executionContext.put("headId", items.get(0).getId());

        super.write(items);
    }
}
