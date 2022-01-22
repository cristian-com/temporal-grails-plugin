package grails.plugin.temporal

class BeanArgsWrapper {

    @Delegate
    final List<Object> args

    private BeanArgsWrapper(Object... args) {
        if (args){
            this.args = Arrays.asList(args).asImmutable()
        } else {
            this.args = Collections.emptyList()
        }
    }

    static BeanArgsWrapper of(Object... args) {
        return new BeanArgsWrapper(args)
    }

}
