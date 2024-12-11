KafkaProject flow:
---
App -> kafka -> kafka-streams -> kafka connect sink to PostgreSQL

Description
---
ConversationGenerator writes 'user messages' to user_messages kafka topic (see AKHQ)  
BadWordStreamer - kafka streams application, which looks for banwords in user messages from kafka topic  
and sends them to a new kafka topic user_message_violations (see AKHQ)  
Sink connector writes data from user_message_violations kafka topic into banword_violations table in PostgreSQL.

How-to
---
How to start the whole flow in Docker:

- Go to containers folder
```
cd containers
```

- Run docker containers
```
docker-compose up -d --build
```

- Create sink connector using the following curl:
```
curl 'http://localhost:8000/api/connect-testing1/connectors' \
-H 'Accept: application/json' \
-H 'Accept-Language: en-GB,en-US;q=0.9,en;q=0.8' \
-H 'Cache-Control: no-cache' \
-H 'Connection: keep-alive' \
-H 'Content-Type: application/json' \
-H 'Origin: http://localhost:8000' \
-H 'Pragma: no-cache' \
-H 'Referer: http://localhost:8000/' \
-H 'Sec-Fetch-Dest: empty' \
-H 'Sec-Fetch-Mode: cors' \
-H 'Sec-Fetch-Site: same-origin' \
--data-raw '{"name":"sink-user_message_violations","config":{"connector.class":"io.confluent.connect.jdbc.JdbcSinkConnector","dialect.name":"PostgreSqlDatabaseDialect","table.name.format":"banword_violations","connection.password":"test_pwd","topics":"user_message_violations","connection.attempts":"3","connection.backoff.ms":"3000","value.converter.schema.registry.url":"http://schema-registry:8081","auto.evolve":"True","connection.user":"test_user","db.timezone":"UTC","auto.create":"True","value.converter":"io.confluent.connect.avro.AvroConverter","connection.url":"jdbc:postgresql://postgres_container:5432/testing_DB?currentSchema=public","key.converter.schema.registry.url":"http://schema-registry:8081","key.converter":"org.apache.kafka.connect.storage.StringConverter","pk.mode":"none","pk.fields":"none"}}'
```

- Open PgAdmin page (see the link below, try 'test_pwd' master pass), create new connection with the following parameters:  
host - postgres_container  
port - 5432  
maintenance database - testing_DB  
user - test_user  
password - test_pwd

- Run the following query. It shows the data saved by connector from user_message_violations kafka topic.  
The table should slightly grow as per new data in kafka topics.  
```
SELECT violated_with, violated_by FROM public.banword_violations;
```

Useful UI links:
---
PgAdmin - http://localhost:5050/browser/  
UI Kafka Connect - http://localhost:8000/#/cluster/connect-testing1  
AKHQ - http://localhost:8086/ui/kafka-cluster/topic
