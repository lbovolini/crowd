![CI](https://github.com/lbovolini/crowd/workflows/CI/badge.svg)  [![Coverage Status](https://coveralls.io/repos/github/lbovolini/crowd/badge.svg?branch=main&kill_cache=1)](https://coveralls.io/github/lbovolini/crowd?branch=main) [![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

Java Asynchronous Non Blocking Distributed Computing Library

## Features

- Distributed and parallel master worker computing
- Supports remote class loading
- Supports remote native library loading (.so, .dll)
- Devices can enter or leave computation at any time
- Fault tolerance mechanism via exceptions and timeouts
- ~~Provides runtime conversion of java class file to Android dex format~~
- Modular architecture
- No configuration required

## Requirements

This library requires some software to provide access to resources (class files, jar packages and native libraries) if any is required by clients, like **Lighttp**.

## Properties

```bash
codebase.url
```

Defines the URL that identifies the location of class and jar resources.

Default value is "file:" 

Required for remote class loading

Server only

[//]: # (Module: ...crowd.discovery Class: CodebaseUtils)



```bash
codebase.root
```

Points to root path of resources.

Default value is some temporary directory path

Required for remote resource loading

Server only

[//]: # (Module: ...crowd.discovery Class: CodebaseUtils)



```
lib.url
```

Defines the URL that identifies the location native library resources.

Default value is "file:" 

Required for remote native library loading

Server only

[//]: # (Module: ...crowd.discovery Class: CodebaseUtils)

<details>
<summary>Show All</summary>

<p>   


```
codebase.url.separator
```

Default value is: " "

[//]: # (Module: ...crowd.discovery Class: URLUtils)

```
classloader.custom
```

Tells application to use a custom classloader defined in "classloader" property below.

Default value is: "False"

```
classloader
```

This property defines the canonical class name of alternative classloader to be used if "classloader.custom" property is true.  

~~Default value is "com.github.lbovolini.crowd.android.classloader.AndroidRemoteClassLoader"~~

~~Optional, use only if alternative classloader, that is not AndroidRemoteClassLoader, is required.~~

Client only

[//]: # (Module: ...crowd.classloader Class: RemoteClassLoaderService)



```
cache
```

See: https://docs.oracle.com/javase/7/docs/api/java/net/URLConnection.html#setUseCaches(boolean)

Default value is "false"

[//]: # (Module: ...crowd.classloader Class: FileDownloader)



```
pool.size
```

Default value is: See https://docs.oracle.com/javase/7/docs/api/java/lang/Runtime.html#availableProcessors()

[//]: # (Module: ...crowd.client Class: ClientRequestHandler)



```
port
```

Defines the value of TCP port used during client channel binding 

Default value is "8081"

[//]: # (Module: ...crowd.client Class: ClientWorkerFactory)



```
multicast.ip
```



Defines the multicast ip address

Default value is "225.4.5.6"

Warning: This is a shared property between client and server, thus, must be equal in both

Required

[//]: # (Module: ...crowd.client Class: MulticastClientWorkerFactory)



```
multicast.interface
```

Defines the name of local network interface that will be used by multicast channel group

Default value is: See com.github.lbovolini.crowd.core.util.HostUtils.getNetworkInterfaceName()

Required 

[//]: # (Module: ...crowd.client Class: MulticastClientWorkerFactory)



```
multicast.server.port
```

Defines the value of UDP server port

Default value is "8000"

Warning: This is a shared property between client and server, thus, must be equal in both

Required

[//]: # (Module: ...crowd.client Class: MulticastClientWorkerFactory)



```
multicast.client.port
```



Defines the value of UDP port used during client channel binding 

Default value is "8011"

Required

[//]: # (Module: ...crowd.client Class: MulticastClientWorkerFactory)



```
class.path
```

Defines the absolute path of where downloaded class and jar files will be placed

Default value is: Value of java.io.tmpdir property

[//]: # (Module: ...crowd.client Class: Agent)



```
lib.path
```

Defines the absolute path of where downloaded native library files will be placed

Default value is: Value of java.io.tmpdir property

[//]: # (Module: ...crowd.client Class: Agent)



// !TODO

```
codebase.root
```

[//]: # (Module: ...crowd.server Class: MulticastServerWorker)




```
multicast.ip
```

[//]: # (Module: ...crowd.server Class: MulticastServerWorkerFactory)



```
multicast.server.port
```

[//]: # (Module: ...crowd.server Class: MulticastServerWorkerFactory)



```
multicast.interface
```

[//]: # (Module: ...crowd.server Class: MulticastServerWorkerFactory)



```
hostname
```

Hostname used during bind of TCP channel

Default value is:  HostUtils.getHostAddressName()

[//]: # (Module: ...crowd.server Class: Crowd)



```
port
```

Default value is: "8081"

[//]: # (Module: ...crowd.server Class: Crowd)



```
dex.version
```

~~Represents the minimum SDK version of Android API used by dex bytecode converter.~~

~~Default value is: "26"~~

~~Android only~~

[//]: # (Module: ...crowd.classloader.android Class: AndroidRemoteClassLoader)



```
dex.optimize
```

~~Tels if dex bytecode should be optimized.~~

~~Android only~~

~~Default value is: "true"~~

[//]: # (Module: ...crowd.classloader.android Class: AndroidRemoteClassLoader)

</p>
</details>  
<br/>

## How to use

- See Wiki


