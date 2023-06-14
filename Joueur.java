
class Joueur {
    private String nom; 
    private int score;
    private boolean token;
    public Joueur(String nom){
        this.nom=nom;
        this.score=0;
        this.token=true;// pour que un joueur puisse joué il faut que son token soit a true
    }
    public void increase_score(){
        System.out.println("le joueur "+this.nom+" a gagne cette manche \\(^_^)/");
        this.score++;
    }
    public String get_nom(){
        return this.nom;
    }
    public int get_score(){
        return this.score;
    }
    public boolean peu_jouer(){
        return token;
    }
    public void set_token(boolean b){
        this.token=b;
    }

    public String toString(){
        StringBuilder sb=new StringBuilder();
        sb.append("nom : ");
        sb.append(this.nom);
        sb.append(" | ");
        sb.append("Score : ");
        sb.append(this.score);
        sb.append("peut jouer :");
        sb.append(this.token);
        sb.append("\n");
        return sb.toString();
    }
}
