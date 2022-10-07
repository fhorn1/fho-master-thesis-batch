package org.goafabric.spring.boot.examplebatch.ops;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.goafabric.spring.boot.examplebatch.domain.OpsCode;
import org.goafabric.spring.boot.examplebatch.domain.OpsHead;
import org.goafabric.spring.boot.examplebatch.domain.OpsImportTuple;
import org.goafabric.spring.boot.examplebatch.domain.Person;
import org.goafabric.spring.boot.examplebatch.ops.writer.OpsCodeItemWriter;
import org.goafabric.spring.boot.examplebatch.ops.writer.OpsHeadItemWriter;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.List;

@Slf4j
@Setter
public class OpsCatalogItemWriter<T> implements ItemWriter<T>, InitializingBean {
    private ItemWriter<OpsHead> opsHeadItemWriter;
    private ItemWriter<OpsCode> opsCodeItemWriter;

    @Override
    public void write(List<? extends T> items) throws Exception {
        T importTuple = items.get(0);
        if (importTuple instanceof OpsImportTuple opsImportTuple) {
            opsHeadItemWriter.write(Collections.singletonList(opsImportTuple.getOpsHead()));
            opsCodeItemWriter.write(opsImportTuple.getOpsCodes());
        }
    }

    @Override
    public void afterPropertiesSet() {
        Assert.notNull(opsHeadItemWriter, "Ops Head writer is missing.");
        Assert.notNull(opsCodeItemWriter, "Ops Code writer is missing.");
    }
}
