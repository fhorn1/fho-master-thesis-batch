package org.goafabric.spring.boot.examplebatch.ops.writer;

import org.goafabric.spring.boot.examplebatch.domain.OpsCode;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemWriter;

import java.util.ArrayList;
import java.util.List;

public class ListUnpackingOpsCodeItemWriter<T> implements ItemWriter<List<T>> {

    private ItemWriter<T> delegate;

    @BeforeStep
    public void retrieveInterstepData(StepExecution stepExecution) {
        JobExecution jobExecution = stepExecution.getJobExecution();
        ExecutionContext jobContext = jobExecution.getExecutionContext();
        if (delegate instanceof OpsCodeItemWriter) {
            ((OpsCodeItemWriter) delegate).setHeadId((String) jobContext.get("headId"));
        }
    }

    @Override
    public void write(final List<? extends List<T>> lists) throws Exception {
        final List<T> consolidatedList = new ArrayList<>();
        for (final List<T> list : lists) {
            consolidatedList.addAll(list);
        }
        delegate.write(consolidatedList);
    }

    public void setDelegate(ItemWriter<T> delegate) {
        this.delegate = delegate;
    }

}