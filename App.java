import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;


public class App {
    public static void main(String argv[]) {
        if (argv.length<2){
            System.out.println("Veuillez saisir des joueurs App {pseudo}");
            return;
        }
        Map<String, Joueur> map_joueurs = new HashMap<>();
        joueur_to_map(map_joueurs, argv);
        Podium p_rouge = new Podium("ROUGE");
        Podium p_bleu = new Podium("BLEU");
        final int MAX_ANIMAUX = p_bleu.get_max_animaux();
        Animaux liste_Animaux[] = generate_animaux(MAX_ANIMAUX);
        ArrayList<Carte> liste_cartes = generate_carte(liste_Animaux, p_bleu, p_rouge);

        Map<String, Instruction> map_instructions = set_map_instructions(p_bleu, p_rouge);
        Carte carte_init = get_random_carte(liste_cartes);
        Carte carte_destination = get_random_carte(liste_cartes);
        String joueur_solution;

        ArrayList<Joueur> joueur_en_jeu = new ArrayList<>(map_joueurs.values());
        String nom_joueur;
        Joueur current_player;

        while (liste_cartes.size() > 0) {
            affichage_jeu(carte_init, carte_destination, MAX_ANIMAUX);

            System.out.println("votre solution >> ");
            joueur_solution = get_solution_joueur(map_joueurs, map_instructions);
            try {
                nom_joueur = joueur_solution.substring(0, joueur_solution.lastIndexOf(" "));
                current_player = map_joueurs.get(nom_joueur);
            } catch (Exception e) {
                continue;
            }
            if (!analyse_solution(map_instructions, map_joueurs, joueur_solution, carte_init, carte_destination)) {
                joueur_en_jeu.remove(current_player);
            } 
            else {
                set_all_player_token_true(map_joueurs);
                joueur_en_jeu = new ArrayList<>(map_joueurs.values());
                carte_init = carte_destination;
                carte_destination = get_random_carte(liste_cartes);
            }

            if (joueur_en_jeu.size() == 1) {
                joueur_en_jeu.get(0).increase_score();
                set_all_player_token_true(map_joueurs);
                joueur_en_jeu = new ArrayList<>(map_joueurs.values());
                carte_destination = get_random_carte(liste_cartes);
            }
        }
        afficher_score(map_joueurs);

    }
/**
 * 
 * @param NB_MAX_ANIMAUX le nmbre maximum d'animaux que peu contenir une pile
 * @return  retourne liste avec tout les animaux dedans ( lion ours elephant )
 */
    public static Animaux[] generate_animaux(int NB_MAX_ANIMAUX) {
        Animaux liste_Animaux[] = new Animaux[NB_MAX_ANIMAUX];

        String noms_animaux[] = new String[liste_Animaux.length];
        noms_animaux[0] = "LION";
        noms_animaux[1] = "OURS";
        noms_animaux[2] = "ELEPHANT";
        normalise_nom_animaux(noms_animaux);

        Animaux a_tmp;

        for (int i = 0; i < noms_animaux.length; i++) {
            a_tmp = new Animaux(noms_animaux[i]);
            liste_Animaux[i] = a_tmp;
        }
        return liste_Animaux;

    }
    /**
     * 
     * @param noms_animaux liste qui contient le nom de tout les animaux 
     * cette fonction met des espaces en debut et fin du nom des animaux de sorte a avoir meilleur affichage
     */
    public static void normalise_nom_animaux(String noms_animaux[]) {
        assert noms_animaux.length != 0;
        int max_len = -1;
        int name_len;
        for (int i = 0; i < noms_animaux.length; i++) {
            name_len = noms_animaux[i].length();
            if (name_len > max_len) {
                max_len = name_len;
            }
        }
        int nb_blank;
        StringBuilder sb;

        for (int i = 0; i < noms_animaux.length; i++) {
            sb = new StringBuilder();
            name_len = noms_animaux[i].length();

            nb_blank = max_len - name_len;
            nb_blank = nb_blank / 2;
            for (int j = 0; j < nb_blank; j++) {
                sb.append(" ");
            }
            sb.append(noms_animaux[i]);
            for (int k = 0; k < nb_blank; k++) {
                sb.append(" ");
            }
            noms_animaux[i] = sb.toString();
        }
    }
    /**
     * 
     * @param map_joueurs une hashmap qui va contenir les joueur qui a pour clé le nom du joueur et comme valeur le joueur
     * @param arv liste des nom des joueurs saisie au lancement du programme 
     */
    public static void joueur_to_map(Map<String, Joueur> map_joueurs, String arv[]) {
        Joueur j_tmp;
        String j_nom;
        for (int j = 0; j < arv.length; j++) {
            j_nom = arv[j];
            if (map_joueurs.containsKey(j_nom)) {

                System.out.println("Le joueur existe deja :/");
                continue;
            }
            j_tmp = new Joueur(j_nom);
            map_joueurs.put(j_nom, j_tmp);
        }

    }
    /**
     * 
     * @param p_bleu le podium bleu 
     * @param p_rouge le podium rouge
     * @return une hasmap qui a pour clé le chaine de charactere de l'instruction et comme valeur l'instruction a executé
     * cette fonction nous permmetera de faire directement le lien entre l'instruction sous forme de chaine de caractère 
     * et l'instruction à executé on pourra appelé les instructions sous la forme map_instruction.get(instructions).run(p_bleu,p_rouge)
     * 
     * La class Instruction est une class abstraite qu'on implemente à chaques nouvelles instructions
     */
    public static Map<String, Instruction> set_map_instructions(Podium p_bleu, Podium p_rouge) {
        Map<String, Instruction> set_instructions = new HashMap<>();
        
        set_instructions.put("KI", new Instruction() {
            public void run(Podium p_bleu, Podium p_rouge) {
                if (!p_bleu.est_vide() && !p_rouge.est_pleine()) {
                    p_bleu.jump_to(p_rouge);

                }
            }
        });

        set_instructions.put("LO", new Instruction() {
            public void run(Podium p_bleu, Podium p_rouge) {
                if (!p_rouge.est_vide() && !p_bleu.est_pleine()) {
                    p_rouge.jump_to(p_bleu);
                }

            }
        });
        set_instructions.put("SO", new Instruction() {
            public void run(Podium p_bleu, Podium p_rouge) {
                if (!p_bleu.est_vide() && !p_rouge.est_vide()) {
                    p_rouge.permut_top(p_bleu);
                }

            }
        });
        set_instructions.put("NI", new Instruction() {
            public void run(Podium p_bleu, Podium p_rouge) {
                if (!p_bleu.est_vide()) {
                    p_bleu.last_to_top();
                }

            }
        });
        set_instructions.put("MA", new Instruction() {
            public void run(Podium p_bleu, Podium p_rouge) {
                if (!p_rouge.est_vide()) {
                    p_rouge.last_to_top();
                }

            }
        });

        return set_instructions;
    }
    /**
     * 
     * @param liste_carte liste contenant les 24 cartes possibles
     * @return une carte aléatoirement parmis @liste_carte
     */
    public static Carte get_random_carte(ArrayList<Carte> liste_carte) {
        Random r = new Random();
        int random_int = r.nextInt(0, liste_carte.size());
        Carte c_tmp = liste_carte.get(random_int);
        liste_carte.remove(random_int);
        return c_tmp;

    }
    /**
     * 
     * @param map_joueurs hash map contenant les joueurs
     * @param map_instruction hash map contenant les instructions
     * @return  Retourne la sequence d'instruction(<nom_joueur> <sequence>) que le joueur à saisie si elle est syntaxiquement correct , return "NO" sinon
     */
    public static String get_solution_joueur(Map<String, Joueur> map_joueurs,Map<String, Instruction> map_instruction) {
        
        Scanner sc = new Scanner(System.in);
        String joueur_solution = sc.nextLine();
        String nom_joueur;
        String solution;
        try {
            nom_joueur = joueur_solution.substring(0, joueur_solution.lastIndexOf(" "));
            solution = joueur_solution.substring(joueur_solution.lastIndexOf(" ") + 1);
        } catch (Exception e) {
            System.out.println("commande non reconnue , veuillez saisir <nom_joueur> <sequence>");
            return "NO";
        }

        String instruction;

        // verification si le joueur peu joué
        if (!map_joueurs.containsKey(nom_joueur)) {
            System.out.println("le joueur " + nom_joueur + " n'existe pas ! :/");
            return "NO";
        } else if (!map_joueurs.get(nom_joueur).peu_jouer()) {
            System.out.println("Le joueur " + nom_joueur + " ne peu pas joue :/");
            return "NO";
        }
        // verification si la séquence est bonne
        if (solution.length() % 2 != 0) {
            System.out.println("Instructions non reconnu ! :(");
            return "NO";
        }
        for (int i = 0; i < solution.length(); i += 2) {
            instruction = solution.substring(i, i + 2);
            if (!map_instruction.containsKey(instruction)) {
                System.out.println("Instructions non reconnu ! :(");
                return "NO";
            }
        }
        return joueur_solution;

    }
    /**
     * 
     * @param map_joueurs hash map contenant les joueurs
     * @param map_instruction hash map contenant les instructions
     * @param joueur_solution chaine de caractère de la forme "nomJoueur solution"
     * @param carte_init la carte initial
     * @param carte_a_atteindre la carte a atteindre
     * @return return true si la solution est correct false sinon
     * cette fonction analyse la solution proposé par un joueur en simulant sur des podium cloné si la solution est correct
     */
    public static boolean analyse_solution(Map<String, Instruction> map_instruction, Map<String, Joueur> map_joueurs,
            String joueur_solution, Carte carte_init, Carte carte_a_atteindre) {
        if (joueur_solution.equals("NO")) {
            return false;
        }
        String instruction;
        Podium p_bleu_init = carte_init.get_podium1().clone();
        Podium p_rouge_init = carte_init.get_podium2().clone();

        Podium p_bleu_a_att = carte_a_atteindre.get_podium1().clone();
        Podium p_rouge_a_att = carte_a_atteindre.get_podium2().clone();

        String nom_joueur = joueur_solution.substring(0, joueur_solution.lastIndexOf(" "));
        String solution = joueur_solution.substring(joueur_solution.lastIndexOf(" ") + 1);

        for (int i = 0; i < solution.length(); i += 2) {
            instruction = solution.substring(i, i + 2);
            map_instruction.get(instruction).run(p_bleu_init, p_rouge_init);

        }

        if (p_bleu_init.is_equal(p_bleu_a_att) && p_rouge_init.is_equal(p_rouge_a_att)) {
            map_joueurs.get(nom_joueur).increase_score();
            return true;
        } else {
            map_joueurs.get(nom_joueur).set_token(false);
            System.out.println("Mauvaise solution noob\n");
            return false;
        }

    }
    /**
     * 
     * @param map_joueurs hasmap contenant tout les joueurs
     * cette fonction fait en sorte que tout les joueurs puisse jouer ( que leur token soit a true )
     */
    public static void set_all_player_token_true(Map<String, Joueur> map_joueurs) {

        for (Joueur joueur : map_joueurs.values()) {
            joueur.set_token(true);
        }
    }
    /**
     * 
     * @param liste_Animaux liste contenant tout les animaux 
     * @param p_bleu   podium bleu
     * @param p_rouge  podium rouge
     * @return une arraylist des 24 combinaisons de cartes possible
     */
    public static ArrayList<Carte> generate_carte(Animaux liste_Animaux[], Podium p_bleu, Podium p_rouge) {
        assert liste_Animaux.length != 0;
        int nb_animaux = liste_Animaux.length;

        ArrayList<Carte> liste_carte = new ArrayList<>();

        for (int i = 0; i < nb_animaux; i++) {
            p_bleu.push(liste_Animaux[i]);
        }

        for (int i = 0; i < 6; i++) {
            liste_carte.add(new Carte(p_bleu.clone(), p_rouge.clone()));
            p_bleu.jump_to(p_rouge);
            liste_carte.add(new Carte(p_bleu.clone(), p_rouge.clone()));
            p_bleu.permut(p_rouge);
            p_bleu.last_to_top();
        }
        p_bleu.clear();
        p_rouge.clear();
        p_bleu.push(liste_Animaux[0]);
        p_bleu.push(liste_Animaux[2]); // pas très propre ¯\_(ツ)_/¯
        p_bleu.push(liste_Animaux[1]);

        for (int i = 0; i < 6; i++) {
            liste_carte.add(new Carte(p_bleu.clone(), p_rouge.clone()));
            p_bleu.jump_to(p_rouge);
            liste_carte.add(new Carte(p_bleu.clone(), p_rouge.clone()));
            p_bleu.permut(p_rouge);
            p_bleu.last_to_top();
        }
        p_bleu.clear();
        p_rouge.clear();
        return liste_carte;
    }
    /**
     * 
     * @param carte_initial
     * @param carte_a_atteindre
     * @param NB_MAX_ANIMAUX
     * affiche le jeu
     */
    public static void affichage_jeu(Carte carte_initial, Carte carte_a_atteindre, int NB_MAX_ANIMAUX) {
        // podiums de la carte initial
        Podium p_bleu_init = carte_initial.get_podium1();
        Podium p_rouge_init = carte_initial.get_podium2();

        // podiums de la carte a atteindre
        Podium p_bleu_a_att = carte_a_atteindre.get_podium1();
        Podium p_rouge_a_att = carte_a_atteindre.get_podium2();
        StringBuilder sb = new StringBuilder();
        for (int i = NB_MAX_ANIMAUX; i >= 0; i--) {
            sb.append("\n");

            // draw starts podiums
            if (i > p_bleu_init.get_nb_animaux()) {
                sb.append(String.format("%10s", " "));
            } else if (!p_bleu_init.est_vide()) {
                sb.append(String.format("%10s", p_bleu_init.pop().get_nom()));
            }

            if (i > p_rouge_init.get_nb_animaux()) {
                sb.append(String.format("%10s", " "));
            } else if (!p_rouge_init.est_vide()) {
                sb.append(String.format("%10s", p_rouge_init.pop().get_nom()));
            }
            if (i > 0) {
                sb.append(String.format("%5s", " "));

            }

            // draw destinations podiums
            if (i > p_bleu_a_att.get_nb_animaux()) {
                sb.append(String.format("%10s", " "));
            } else if (!p_bleu_a_att.est_vide()) {
                sb.append(String.format("%10s", p_bleu_a_att.pop().get_nom()));
            }

            if (i > p_rouge_a_att.get_nb_animaux()) {
                sb.append(String.format("%10s", " "));
            } else if (!p_rouge_a_att.est_vide()) {
                sb.append(String.format("%10s", p_rouge_a_att.pop().get_nom()));
            }
            // pas très propre ¯\_(ツ)_/¯

        }
        String trait_podium = "----";
        sb.append(String.format("%8s %2s %6s %5s %8s %2s %6s\n", trait_podium, " ", trait_podium, "==>", trait_podium,
                " ", trait_podium));
        sb.append(String.format("%8s %2s %6s %5s %8s %2s %6s\n", p_bleu_init.get_couleur(), " ",
                p_rouge_init.get_couleur(), " ", p_bleu_a_att.get_couleur(), " ", p_rouge_a_att.get_couleur()));
        sb.append("____________________________________________\n");
        sb.append("KI : BLEU --> ROUGE    NI : BLEU  ^\nLO : BLEU <-- ROUGE    MA : ROUGE  ^\nSO : BLEU <-> ROUGE\n");

        System.out.println(sb.toString());
    }
    /**
     * 
     * @param map_joueur
     * affiche le score board
     */
    public static void afficher_score(Map<String, Joueur> map_joueur) {

        Joueur j_tmp;
        ArrayList<Joueur> tableau_score_joueur = new ArrayList<>(map_joueur.values());
        int min_index;
        for (int i = 0; i < tableau_score_joueur.size() - 1; i++) {
            min_index = i;
            for (int j = i + 1; j < tableau_score_joueur.size(); j++) {

                if (tableau_score_joueur.get(j).get_score() > tableau_score_joueur.get(min_index).get_score()) {
                    min_index = j;
                } else if (tableau_score_joueur.get(j).get_score() == tableau_score_joueur.get(min_index).get_score()) {
                    if (tableau_score_joueur.get(j).get_nom()
                            .compareTo(tableau_score_joueur.get(min_index).get_nom()) < 0) {
                        min_index = j;
                    }
                }
            }
            j_tmp = tableau_score_joueur.get(min_index);
            tableau_score_joueur.set(min_index, tableau_score_joueur.get(i));
            tableau_score_joueur.set(i, j_tmp);
        }

        StringBuilder sb = new StringBuilder();
        String barre = "-------------------------------\n";

        sb.append(barre);
        sb.append(String.format("|%9s %1s %5s %7s %3s\n", "NOM", "", "|", "SCORE", "|"));
        sb.append(barre);

        for (Joueur joueur : tableau_score_joueur) {

            sb.append(String.format("|%10s %s %5s %5s %5s", joueur.get_nom(), "", "|", joueur.get_score(), "|"));
            sb.append("\n");
        }
        sb.append(barre);
        System.out.println(sb);

    }

}