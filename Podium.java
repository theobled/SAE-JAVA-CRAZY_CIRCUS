import java.lang.StringBuilder;

class Animaux{
    //la class doit s'appeller "Animal" mais jme suis rendu compte trop tard de ma faute de grammaire :/ 
    private String nom;
    public Animaux(String nom){
        this.nom=nom;
    }
    public String get_nom(){
        return this.nom;
    }
    public void set_nom(String nom ){
        this.nom=nom;
    }
}

public class Podium {
    private String couleur;
    private Animaux animal_stack[];
    private int nb_animaux;
    private static final int MAX_ANIMAUX=3;

    public Podium(){
        this.couleur=" ";
        this.animal_stack=new Animaux[Podium.MAX_ANIMAUX];
        this.nb_animaux=0;
    }
    public int get_max_animaux(){
        return Podium.MAX_ANIMAUX;
    }
    public Podium(String couleur ){
        this();
        this.couleur=couleur; 
    }
    public void clear(){
        this.nb_animaux=0;
    }
    public void push(Animaux a){
        assert !this.est_pleine();
        this.animal_stack[this.nb_animaux]=a;
        this.nb_animaux++;
    }
    public boolean is_equal(Podium p){
        if(p.get_nb_animaux()!=this.get_nb_animaux()){
            return false;
        }
        
        for (int i = 0; i < p.get_nb_animaux(); i++) {
            if(!this.animal_stack[i].get_nom().equals(p.animal_stack[i].get_nom())){
                return false;
            }
        }
        return true;
    }
    public Animaux pop(){
        assert !this.est_vide() : "empty stack ";
        Animaux a_tmp;
        a_tmp=this.animal_stack[this.nb_animaux-1];
        this.nb_animaux--;

        return a_tmp;
    }
    public boolean est_vide(){
        return this.nb_animaux==0;
    }
    public boolean est_pleine(){
        return this.nb_animaux>Podium.MAX_ANIMAUX;
    }
    public void last_to_top(){
        // INSTRUCTION NI MA
        assert !this.est_vide():"la pile " +this.couleur+" est vide";

        Podium podium_tmp=new Podium();
        Animaux animaux_tmp;
        for (int i = this.nb_animaux-1; i >=0 ; i--) {
            podium_tmp.push(this.pop());
        }
        animaux_tmp=podium_tmp.pop();
        for (int i = podium_tmp.nb_animaux-1; i >=0; i--) {
            this.push(podium_tmp.pop());
        }
        this.push(animaux_tmp);
        
    }
    public void jump_to(Podium P){
        // INSTRUCTION KI LO
        assert !this.est_vide():"la pile source est vide";
        assert !P.est_pleine():"la pile cible est plaine";

        P.push(this.pop());
    }
    public void permut_top(Podium P){
        // INSTRUCTION SO
        assert (!P.est_vide() && !this.est_vide());
        Animaux animal_tmp=this.pop();
        this.push(P.pop());
        P.push(animal_tmp);
    }
    public void permut(Podium P){
        assert (!P.est_vide() || !this.est_vide());
        Podium p_tmp1=new Podium();
        Podium p_tmp2=new Podium();
      

        for (int i = 0; i < Podium.MAX_ANIMAUX; i++) {
            if (!P.est_vide()) {
                p_tmp1.push(P.pop());
            }
            if(!this.est_vide()){
                p_tmp2.push(this.pop());
            }
        }
        for (int i = 0; i < Podium.MAX_ANIMAUX; i++) {
            if (!p_tmp1.est_vide()) {
                this.push(p_tmp1.pop());
            }
            if(!p_tmp2.est_vide()){
                P.push(p_tmp2.pop());

            }
            
        }
    }
    public Animaux get_Animaux(int index){
        assert index<=this.nb_animaux && index>=0;
        return this.animal_stack[index];
    }
    public int get_nb_animaux(){
        return this.nb_animaux;
    }
    public String get_couleur(){
        return this.couleur;
    }
    public void set_couleur(String couleur){
        this.couleur=couleur;
    }
    public Podium clone(){
        Podium p_tmp=new Podium(this.couleur);
        p_tmp.nb_animaux=this.nb_animaux;
        p_tmp.animal_stack=this.animal_stack.clone();

        return p_tmp;
    }
    public String toString(){
        StringBuilder sb=new StringBuilder();
        for (int i = this.nb_animaux-1; i >=0 ; i--) {
            sb.append(this.animal_stack[i].get_nom());
            sb.append("\n");   
        }
        sb.append("--------\n");
        sb.append(this.couleur);
        return sb.toString();
    }

}
