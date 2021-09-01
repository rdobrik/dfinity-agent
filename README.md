# dfinity-agent
Dfinity Agent is a set of native java libriaries to connect remotely to Dfinity Internet Computer applications.

<a href="https://dfinity.org/">
https://dfinity.org/
</a>

The code is implementation of Internet Computer Interface protocol 

<a href="https://sdk.dfinity.org/docs/interface-spec/index.html">
https://sdk.dfinity.org/docs/interface-spec/index.html
</a>

and it's using Dfinity Rust agent as an inspiration, using similar package structures and naming conventions.

<a href="https://github.com/dfinity/agent-rs">
https://github.com/dfinity/agent-rs
</a>

Currently we support query and update (call) operations with primitive types, arrays, option and principal type. Record and Variant types support is in development.

## License

Dfinity Agent is available under Apache License 2.0.

## Documentation

Supported type mapping between Java and Candid

| Candid      | Java    |
| :---------- | :---------- | 
| bool   | Boolean | 
|  int| Integer   | 
| int8   | Byte | 
| int16   | Short | 
| int32   | Integer | 
| int64   | Long | 
| float32   | Float | 
| float64   | Double | 
| text   | String | 
| opt   | Optional | 
| principal   | Principal | 
| vec   | array | 
| null   |Null | 


## Get Started



## Downloads / Accessing Binaries

To add Java Dfinity Agent library to your Java project use Maven or Gradle import from Maven Central.

```
<dependency>
  <groupId>com.scaleton.dfinity</groupId>
  <artifactId>dfinity-agent</artifactId>
  <version>0.5.2</version>
</dependency>
```

```
implementation 'com.scaleton.dfinity:dfinity-agent:0.5.2'
```

## Build

You need JDK 8+ to build Dfinity Agent.

