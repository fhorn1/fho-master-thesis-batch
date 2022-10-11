#Documentation
https://docs.spring.io/spring-batch/docs/current/reference/html/index.html
https://docs.spring.io/spring-batch/docs/current/reference/html/job.html#configureJob
https://docs.spring.io/spring-batch/docs/current/reference/html/readersAndWriters.html#xmlReadingWriting

#SQLS
select * from catalogs.batch_job_execution
select * from catalogs.toy_catalog
select * from catalogs.person_catalog

docker run --rm goafabric/ops-import-batch-arm64v8:1.0.0-SNAPSHOT

#docker run
[[ "$(uname -m)" == "arm64"  ]] && ARCH="-arm64v8" && \
docker pull goafabric/ops-import-batch:1.0.0-SNAPSHOT && \
docker run --name spring-boot-example-batch --rm goafabric/ops-import-batch:1.0.0-SNAPSHOT
