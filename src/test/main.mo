actor {
    stable var name = "Me";

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
};
