package studio.brunocasamassa.ajudaquioficial.helper;


public class Conversa {

    private String idUsuario;

    private String nome;

    private String mensagem;

    private String time;

    private int chatCount = 0;


    public Conversa() {
    }

    public String getIdUsuario() {
        return idUsuario;
    }


    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }


    public String getNome() {
        return nome;
    }


    public void setNome(String nome) {
        this.nome = nome;
    }


    public String getMensagem() {

        return mensagem;

    }


    public void setMensagem(String mensagem) {

        this.mensagem = mensagem;

    }


    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getChatCount() {
        return chatCount;
    }

    public void setChatCount(int chatCount) {
        this.chatCount = chatCount;
    }
}
