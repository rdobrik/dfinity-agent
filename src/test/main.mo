import Principal "mo:base/Principal";

actor {
    stable var name = "Me";

    type Entry = {
        bar : Bool;
        foo : Int;
    };

       type ComplexEntry = {
        bar : Bool;
        foo : Int;
        pojo: Entry;
    }; 

       type ComplexArrayEntry = {
        bar : [Bool];
        foo : [Int];
        pojo: [Entry];
    };     

    public func greet(value : Text) : async Text {
        name := value;
        return "Hello, " # name # "!";
    };

    public shared query func getName() : async Text {
        return name;
    };     

    public shared query func peek(name : Text, value : Int) : async Text {
        return "Hello, " # name # "!";
    };

    public shared query func echoText( value : Text) : async Text {
        return value;
    };     

    public shared query func echoInt( value : Int) : async Int {
        return value + 1;
    }; 

     public shared query func echoFloat( value : Float) : async Float {
        return value + 1;
    };       

    public shared query func echoBool( value : Bool) : async Bool {
        return value;
    }; 

    public shared query func echoOption( value : ?Int) : async ?Int {
        return value;
    }; 

    public shared query func echoVec( value : [Int]) : async [Int] {
        return value;
    };

    public shared query func echoPrincipal( value : Principal) : async Principal {
        return value;
    };  

     public shared query func echoRecord( value : Entry) : async Entry {
        return value;
    }; 

     public shared query func echoPojo( value : Entry) : async Entry {
        return value;
    }; 

     public shared query func echoOptionPojo( value : ?Entry) : async ?Entry {
        return value;
    };    

     public shared query func echoComplexPojo( value : ComplexEntry) : async ComplexEntry {
        return value;
    }; 

    public shared query func subComplexPojo( value : ComplexEntry) : async Entry {
        return value.pojo;
    }; 

     public shared query func echoPojoVec( value : [Entry]) : async [Entry] {
        return value;
    }; 

     public shared query func echoComplexPojoVec( value : [ComplexEntry]) : async [ComplexEntry] {
        return value;
    }; 
     public shared query func echoComplexArrayPojo( value : ComplexArrayEntry) : async ComplexArrayEntry {
        return value;
    };                                 
};
 