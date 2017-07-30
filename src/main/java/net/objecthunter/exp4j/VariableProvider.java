package net.objecthunter.exp4j;

public interface VariableProvider {
    Double getDouble(String name);
    boolean hasVariable(String name);

    class Empty implements VariableProvider {

        @Override
        public Double getDouble(String name) {
            return 0.;
        }

        @Override
        public boolean hasVariable(String name) {
            return false;
        }
    }
}
