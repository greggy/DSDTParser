/*
 * Decompiled with CFR 0_115.
 */
package dsdtparser.parser;

public class CompilerError {
    private String mensagem;
    private String tipo;
    private int linha;

    public CompilerError(int line, String type, String message) {
        this.linha = line;
        this.tipo = type;
        this.mensagem = message;
    }

    public String toString() {
        return "[Line " + this.linha + " type " + this.tipo + " message " + this.mensagem + "]";
    }

    public Object[] getTableLine() {
        return new Object[]{new Integer(this.getLinha()), this.getTipo(), this.getMensagem()};
    }

    public String getMensagem() {
        return this.mensagem;
    }

    public String getTipo() {
        return this.tipo;
    }

    public int getLinha() {
        return this.linha;
    }
}

