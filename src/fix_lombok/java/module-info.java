module lombok {
    // just depend on _anything_ to shut up the module resolver
    requires transitive com.google.gson;
}