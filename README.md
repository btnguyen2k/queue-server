Queue Server
============

Queue Server - by btnguyen2k.

Project home: [https://github.com/btnguyen2k/queue-server](https://github.com/btnguyen2k/queue-server).

## Overview ##

Queue service that unifies various queue backends into a single API set.

*Features:*

- Client-server model.
- API sets for Thrift, Thrift-over-HTTP and REST.
- Data format (for REST APIs): JSON
- Simple authorization scheme for API calls.

*Message format (JSON fields)*

- `queue_id`: (long) message's unique id in queue.
- `org_timestamp`: (long) UNIX timestamp (in milliseconds) when the message was first queued.
- `timestamp`: (long) UNIX timestamp (in milliseconds) when the message was queued.
- `num_requeues`: (int) number of times the messaged has been re-queued
- `content`: message's content in base64 encoding.


## Release-notes ##

Latest release: `0.3.0`.

See [RELEASE-NOTES.md](RELEASE-NOTES.md).


## APIs ##

_Each API call is authorized by an `authKey`._

* `initQueue(authkey, queueName)`: creates & initializes a new queue.
* `queue(authkey, queueName, message)`: put a message to a queue.
* `requeue(authkey, queueName, message)`: requeues a message (message's `num_requeues` will be increased).
* `requeueSilently(authkey, queueName, message)`: requeues a message "silently" (message's `num_requeues` will NOT be increased).
* `finish(authKey, queueName, message)`: called when finish processing the message to cleanup ephemeral storage.
* `take(authKey, queueName)`: takes a message from a queue.
* `queueExists(authKey, queueName)`: checks if a queue exists.
* `queueSize(authKey, queueName)`: gets number of items currently in a queue.
* `ephemeralSize(authKey, queueName)`: gets number of items currently in a queue's ephemeral storage.

### REST APIs ###

_All REST API calls must be made via POST method._

* `POST /initQueue`: 
  - Input: JSON `{"secret":"authorization key","queue_name":"name of the queue to be initialized"}`
  - Output: JSON `{"s":200,"r":true ,"m":"Successful: the queue has been created and initialized"}`
  - Output: JSON `{"s":200,"r":false,"m":"Successful: the queue has already existed"}`
  - Output: JSON `{"s":500,"r":false,"m":"Error message: exception occurred at server side"}`
  - Output: JSON `{"s":403,"r":false,"m":"Error message: unauthorized request"}`
  - Output: JSON `{"s":400,"r":false,"m":"Error message: invalid parameters"}`

* `POST /queue`
  - Input: JSON `{"secret":"authorization key","queue_name":"name of the queue", "content":"message content in base64"}`
  - Output: JSON `{"s":200,"r":true ,"m":"Successful: the message has been queued"}`
  - Output: JSON `{"s":500,"r":false,"m":"Error message: exception occurred at server side"}`
  - Output: JSON `{"s":404,"r":false,"m":"Error message: queue does not exist"}`
  - Output: JSON `{"s":403,"r":false,"m":"Error message: unauthorized request"}`
  - Output: JSON `{"s":400,"r":false,"m":"Error message: invalid parameters"}`
  
* `POST /requeue`
  - Input: JSON `{"secret":"authorization key","queue_name":"name of the queue","queue_id":(long),"org_timestamp":(long),"timestamp":(long),"num_requeues":(int),"content":"message content in base64"}`
  - Output: JSON `{"s":200,"r":true ,"m":"Successful: the message has been requeued"}`
  - Output: JSON `{"s":500,"r":false,"m":"Error message: exception occurred at server side"}`
  - Output: JSON `{"s":404,"r":false,"m":"Error message: queue does not exist"}`
  - Output: JSON `{"s":403,"r":false,"m":"Error message: unauthorized request"}`
  - Output: JSON `{"s":400,"r":false,"m":"Error message: invalid parameters"}`

* `POST /requeueSilent`
  - Input: JSON `{"secret":"authorization key","queue_name":"name of the queue","queue_id":(long),"org_timestamp":(long),"timestamp":(long),"num_requeues":(int),"content":"message content in base64"}`
  - Output: JSON `{"s":200,"r":true ,"m":"Successful: the message has been requeued"}`
  - Output: JSON `{"s":500,"r":false,"m":"Error message: exception occurred at server side"}`
  - Output: JSON `{"s":404,"r":false,"m":"Error message: queue does not exist"}`
  - Output: JSON `{"s":403,"r":false,"m":"Error message: unauthorized request"}`
  - Output: JSON `{"s":400,"r":false,"m":"Error message: invalid parameters"}`
  
* `POST /finish`
  - Input: JSON `{"secret":"authorization key","queue_name":"name of the queue","queue_id":(long),"org_timestamp":(long),"timestamp":(long),"num_requeues":(int),"content":"message content in base64"}`
  - Output: JSON `{"s":200,"r":true ,"m":"Successful"}`
  - Output: JSON `{"s":500,"r":false,"m":"Error message: exception occurred at server side"}`
  - Output: JSON `{"s":404,"r":false,"m":"Error message: queue does not exist"}`
  - Output: JSON `{"s":403,"r":false,"m":"Error message: unauthorized request"}`
  - Output: JSON `{"s":400,"r":false,"m":"Error message: invalid parameters"}`

* `POST /take`
  - Input: JSON `{"secret":"authorization key","queue_name":"name of the queue"}`
  - Output: JSON `{"s":200,"r":true ,"m":"Successful","v":{"queue_id":(long),"org_timestamp":(long),"timestamp":(long),"num_requeues":(int),"content":"message's content in base64"}}`
  - Output: JSON `{"s":200,"r":false,"m":"Successful, but queue is empty"}`
  - Output: JSON `{"s":500,"r":false,"m":"Error message: exception occurred at server side"}`
  - Output: JSON `{"s":404,"r":false,"m":"Error message: queue does not exist"}`
  - Output: JSON `{"s":403,"r":false,"m":"Error message: unauthorized request"}`
  - Output: JSON `{"s":400,"r":false,"m":"Error message: invalid parameters"}`

* `POST /queueExists`
  - Input: JSON `{"secret":"authorization key","queue_name":"name of the queue"}`
  - Output: JSON `{"s":200,"r":true ,"m":"true"}}`
  - Output: JSON `{"s":404,"r":false,"m":"false"}`
  - Output: JSON `{"s":500,"r":false,"m":"Error message: exception occurred at server side"}`
  - Output: JSON `{"s":403,"r":false,"m":"Error message: unauthorized request"}`
  - Output: JSON `{"s":400,"r":false,"m":"Error message: invalid parameters"}`

* `POST /queueSize`
  - Input: JSON `{"secret":"authorization key","queue_name":"name of the queue"}`
  - Output: JSON `{"s":200,"r":true ,"v":(int)size}`
  - Output: JSON `{"s":200,"r":false,"v":-1,"m":"Operation is not supported"}`
  - Output: JSON `{"s":500,"r":false,"m":"Error message: exception occurred at server side"}`
  - Output: JSON `{"s":404,"r":false,"m":"Error message: queue does not exist"}`
  - Output: JSON `{"s":403,"r":false,"m":"Error message: unauthorized request"}`
  - Output: JSON `{"s":400,"r":false,"m":"Error message: invalid parameters"}`

* `POST /ephemeralSize`
  - Input: JSON `{"secret":"authorization key","queue_name":"name of the queue"}`
  - Output: JSON `{"s":200,"r":true ,"v":(int)size}`
  - Output: JSON `{"s":200,"r":false,"v":-1,"m":"Operation is not supported"}`
  - Output: JSON `{"s":500,"r":false,"m":"Error message: exception occurred at server side"}`
  - Output: JSON `{"s":404,"r":false,"m":"Error message: queue does not exist"}`
  - Output: JSON `{"s":403,"r":false,"m":"Error message: unauthorized request"}`
  - Output: JSON `{"s":400,"r":false,"m":"Error message: invalid parameters"}`


### Thrift APIs ###

See file [queueserver.thrift](thrift/queueserver.thrift).

### Thrift-over-HTTP APIs ###

Thrift APIs, but over HTTP(s)! URL: `http://server:host/thrift`

### Clients ###

- Java: [https://github.com/btnguyen2k/queue-jclient](https://github.com/btnguyen2k/queue-jclient).


## Installation ##

Note: Java 7+ is required!

### Install from binary ###

- Download binary package from [project release site](https://github.com/btnguyen2k/queue-server/releases).
- Unzip the binary package and copy it to your favourite location, e.g. `/usr/local/queue-server`.


### Install from source ###

- Download application's source, either cloning github project or download the source package from [project release site](https://github.com/btnguyen2k/queue-server/releases).
- Build [Play! Framework](https://www.playframework.com): `play dist`.
- The built binary package is available at `target/universal/queue-server-<version>.zip`. You may copy it to your favourite location, e.g. `/usr/local/queue-server`.


## Start/Stop Queue-Server ##

### Linux ###

Start server with default options:
> `/usr/local/queue-server/conf/server-production.sh start`

Start server with 1024M memory limit, REST & Thrift-over-HTTP APIs on port 18080, Thrift APIs on port 19090
> `/usr/local/queue-server/conf/server-production.sh start -m 1024 -p 18080 -t 19090`

Stop server:
> `/usr/local/queue-server/conf/server-production.sh stop`


## Configurations ##

### Start Script ###

Custom port numbers:
> ***-p port_number***: set port number for REST & Thrift-over-HTTP APIs. Example: *-m 18080*.

> ***-t port_number***: set port number for Thrift APIs. Example: *-t 19090*. Note: *-t 0* will disable Thrift APIs.

Memory limit:
> ***-m mem_in_mb***: set server's memory limit (unit: Megabytes). Example: *-m 1024*.

Custom configuration files:
> ***-c config_file***: set custom application configuration file, relative file is loaded under directory *${app.home}/conf*.
>
> Example *-c abc.conf*: use configuration file *${app.home}/conf/abc.conf*
>
> Example *-c /myapp/conf/abc.conf*: use configuration file */myapp/conf/abc.conf*

> ***-s config_file***: set custom spring configuration file, relative file is loaded under directory *${app.home}/conf*.
>
> Example *-c spring/beans.xml*: use configuration file *${app.home}/conf/spring/beans.xml*
>
> Example *-c /myapp/spring/beans.xml*: use configuration file */myapp/spring/beans.xml*

### SPring Configuration Files ###

Default file: `${app.home}/conf/spring/beans.xml`


## License ##

See [LICENSE.txt](LICENSE.txt) for details. Copyright (c) 2015-2016 btnguyen2k.

Third party libraries are distributed under their own license(s).
