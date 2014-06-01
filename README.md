# vcloudplus
## v0.1 release

vcloudplus supports windows running as service. It can be run in stand alone or cluster based on mysql community edition. 
And you can use other dbms with JDBC driver. If there is no db provided, all nodes will be run in stand alone mode. And all quartz configuration
should be kept same. Current version contains inner scheduler for extra management such as monitor db which will change running mode.

### Folder
* bin:
	* vcloudplus.jar	main jar includes all dependency
	* vpsvr.exe			register vcloudplus into windows system running as service
	* service.bat		register batch (install or uninstall service)
	* svrdebug.bat		debug version, it will enable JMX service (9527), Remote Debug (9528), Heap dump etc.
					you can change it by yourself.
		
* dbTables:
	* db schema for different database, if needs to run in cluster mode, please create import one of these sql statements
		
* config:
	* log4j.properties			log4j configuration
	* quartz_node.properties	quartz single node configuration, change it accordingly 
	* vcloudplus.properties		provide vcloudplus running environment, change it

* dump:
	dump path

* log:
	each log

### Running
- service.bat install (install vcloudplus as a service on windows)
- service.bat uninstall (uninstall the service)
		
### About dependency
- [mysql jdbc connector 5.1.26][]
- [quartz 2.2.1][]
- [slf4j 1.6.6][]
- [log4j 1.2.16][]
- [vcloud java sdk 5.1.0][]
- [c3p0 0.9.1.1][]

[mysql jdbc connector 5.1.26]: http://dev.mysql.com/downloads/connector/j/
[quartz 2.2.1]: http://quartz-scheduler.org/
[slf4j 1.6.6]: http://www.slf4j.org/
[log4j 1.2.16]: http://logging.apache.org/log4j/2.x/
[vcloud java sdk 5.1.0]: https://developercenter.vmware.com/web/sdk/5.1.0/vcloud-java
[c3p0 0.9.1.1]: http://www.mchange.com/projects/c3p0/

## v0.1
### *basic framework*
here is basic framework for version 0.1.

<div style="text-align:center" markdown="1">
![framework 0.1][vcloudplus framework 0.1]
</div>

* `runner` : vcloudplus can be run as service (**only support windows**) or normal app.

* `tasks` : task framework. it refer to task framework in roller and it's not supported well.

* `vcloud` : i want to manage my vcloud app automatically, this is original purpose of this tool.
			but i think it can do more.

* `job scheduler` : job scheduler use scheduler in jdk. this scheduler is used to monitor runtime
					such as changing running mode.
* `event center` : event center is a typical observer mode to handing different events.

* `quartz` : use [quartz][] as core scheduler to schedule jobs.


### *cluster*
cluster management in version 0.1 is based on quartz. using quartz cluster feature to support vcloudplus cluster.

<div style="text-align:center" markdown="1">
![cluster 0.1][vcloudplus cluster 0.1]
</div>


## future plan

cluster management in [quartz][] is weak. i will use [zookeeper][] as cluster manager.
And with zookeeper, it needs GUI (desktop or web) to cotrol cluster

## about

it's a small tool to make things a little bit easy for software engineers and test engineers.
any questions mail [me][] LOL. 

[vcloudplus framework 0.1]: doc/pic/vcloudplus-framework-0.1.jpg
[vcloudplus cluster 0.1]: doc/pic/vcloudplus-cluster-0.1.jpg
[quartz]: http://quartz-scheduler.org/
[zookeeper]: http://zookeeper.apache.org/
[me]: junli@microstrategy.com
