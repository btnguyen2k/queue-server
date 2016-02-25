Queue Server Release Notes
==========================

Queue Server - by btnguyen2k.

2016-02-25: v0.3.2
------------------

- NPE fix

2016-02-18: v0.3.1
------------------

- Support Redis backend (new class `RedisQueueApi`).
- Change `orphanMessageThresholdMs` to 60 seconds.
- Bugs fixed.


2015-06-22: v0.2.0
------------------

- Support PostgreSQL backend (new class `PgSQLQueueApi`).
- Start/Stop script updated: support custom application config & spring config files.
- Document review & fix.


2015-06-16: v0.1.0
------------------
First release:

- REST & Thrift APIs
