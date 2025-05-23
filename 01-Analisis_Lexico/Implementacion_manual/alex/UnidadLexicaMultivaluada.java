//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package alex;

public class UnidadLexicaMultivaluada extends UnidadLexica {
    private String lexema;

    public UnidadLexicaMultivaluada(int fila, int columna, ClaseLexica ent, String lexema) {
        super(fila, columna, ent);
        this.lexema = lexema;
    }

    public String lexema() {
        return this.lexema;
    }

    public String toString() {
        return this.lexema();
    }
}
