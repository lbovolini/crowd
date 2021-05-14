## How to

### Run

- Server

```bash
java -Dport=8006 -Dcodebase.url=http://192.168.0.118/hello -Dcodebase.root=/srv/http/hello/ -Dlib.url=http://192.168.0.118/hello/libs/ -jar crowd-example-hello-master-0.0.1-SNAPSHOT-jar-with-dependencies.jar
```

- Client

```bash
java -jar crowd-client-0.0.1-SNAPSHOT-jar-with-dependencies.jar
```

##### Expected server output

```bash
2 done 0
5 done 4
6 done 5
4 done 3
1 done 1
8 done 7
3 done 2
9 done 8
7 done 6
10 done 9
11 done 10
12 done 11
13 done 12
14 done 13
15 done 14
16 done 15
...
```

##### Expected client output

```bash
1 Hello world
2 Hello world
3 Hello world
4 Hello world
5 Hello world
6 Hello world
7 Hello world
8 Hello world
9 Hello world
10 Hello world
11 Hello world
12 Hello world
13 Hello world
14 Hello world
15 Hello world
16 Hello world
17 Hello world
18 Hello world
19 Hello world
20 Hello world
21 Hello world
22 Hello world
23 Hello world
24 Hello world
25 Hello world
26 Hello world
27 Hello world
28 Hello world
29 Hello world
30 Hello world
31 Hello world
32 Hello world
...
```



