package org.fho.master.thesis.batch.ops.writer;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.fho.master.thesis.batch.domain.OpsCode;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;

import javax.sql.DataSource;
import java.util.List;

@Slf4j
public class OpsCodeItemWriter extends JdbcBatchItemWriter<OpsCode> {
    private final DataSource dataSource;

    private final String sql;

    @Setter
    private String headId;

    public OpsCodeItemWriter(DataSource dataSource, String sql) {
        this.dataSource = dataSource;
        this.sql = sql;
    }

    @BeforeStep
    public void retrieveInterstepData(StepExecution stepExecution) {
        JobExecution jobExecution = stepExecution.getJobExecution();
        ExecutionContext jobContext = jobExecution.getExecutionContext();
        this.setHeadId((String) jobContext.get("headId"));
    }

    @Override
    public void afterPropertiesSet() {
        this.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
        this.setDataSource(dataSource);
        this.setSql(sql);
        super.afterPropertiesSet();
    }

    public void write(List<? extends OpsCode> items) throws Exception {
        items.forEach(item -> item.setHeadId(headId));
        super.write(items);
    }
}
