package src.client;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Main implements Serializable {

    public static void main(String[] args) {

        String filePath = "";
        String reponse = "";
        String pseudo = "";
        Dresseur dresseur = null;
        Scanner sc = new Scanner(System.in);

        System.out.println("Bienvenue dans le monde des Pokémons !");

        while (true) {
            System.out.println("Avez vous un compte ? (y/n)");
            reponse = sc.nextLine();

            if (reponse.equals("y")) {
                System.out.println("Quel est le pseudo de votre sauvegarde ?");
                pseudo = sc.nextLine();
                filePath = "src\\sauvegarde\\" + pseudo + ".txt";

                if (new File(filePath).exists()) {
                    dresseur = Dresseur.charge(filePath);
                    break;
                } else {
                    System.out.println("Ce pseudo n'existe pas !");
                }
            } else if (reponse.equals("n")) {
                System.out.println("Entrez votre pseudo :");
                pseudo = sc.nextLine();
                dresseur = new Dresseur(pseudo);
                filePath = "src\\sauvegarde\\" + pseudo + ".txt";
                dresseur.save(filePath);
                break;
            } else {
                System.out.println("Vous n'avez pas choisi une option valide !");
            }
        }

        System.out.println("Bonjour " + pseudo + "! \n");

        System.out.println("Quel est votre choix de départ ? \n");

        while (true) {
            System.out.println("\n Que voulez-vous faire " + dresseur.getPseudo() + " ?");
            System.out.println("1. Ouvrir une lootbox");
            System.out.println("2. Combattre un autre dresseur");
            System.out.println("3. Voir ses pokemons");
            System.out.println("4. Voir ses bonbons");
            System.out.println("5. Voir son niveau");
            System.out.println("6. Sauvegarder votre partie");
            System.out.println("7. Charger une partie");
            System.out.println("8. Quitter le jeu \n");

            int choix = sc.nextInt();
            sc.nextLine(); // Pour consommer la nouvelle ligne après avoir lu l'entier

            switch (choix) {

                case 1:
                    System.out.println("Vous avez choisi d'ouvrir une lootbox ! \n");

                    // System.out.println("Que voulez vous generer : \n");

                    // System.out.println("1. Un Pokémon");
                    // System.out.println("2. Plusieurs Pokémons \n");

                    // int choix3 = sc.nextInt();
                    // sc.nextLine(); // Pour consommer la nouvelle ligne après avoir lu l'entier

                    // switch (choix3) {
                    // case 1:

                    Pokemon pokemon = PokemonGenerator.generateRandomPokemon();
                    System.out.println(pokemon.toString() + "\n"); // Affiche les détails du Pokémon

                    Bonbon bonbon = new Bonbon(pokemon.getType());
                    dresseur.ajouterBonbon(bonbon);

                    System.out.println("Vous avez reçu un bonbon de type " + bonbon.getType() + "\n");

                    System.out.println("Voulez-vous garder ce Pokemon ? (y/n) \n");
                    reponse = sc.nextLine();

                    if (reponse.equals("y")) {
                        dresseur.ajouterPokemon(pokemon);
                        System.out.println("Vous avez choisi de garder ce Pokémon ! \n");
                        break;

                    } else if (reponse.equals("n")) {

                        System.out.println("Vous avez choisi de jeter " + pokemon.getNom() + " à la poubelle ! \n");
                        break;

                    } else {
                        System.out.println("Vous n'avez pas choisi une option valide !");
                        System.out.println("Voulez-vous le garder ? (y/n)");

                    }

                    break;

                // case 2:

                // System.out.println("Vous avez choisi de générer pleins de pokemons !\n");
                // System.out.println("Combien de pokemons voulez vous générer ?\n");
                // int nb = sc.nextInt();
                // sc.nextLine(); // Pour consommer la nouvelle ligne après avoir lu l'entier
                // for (int i = 0; i < nb; i++) {
                // Pokemon pokGen = PokemonGenerator.generateRandomPokemon();
                // System.out.println(pokGen.toString() + "\n"); // Affiche les détails du
                // Pokémon
                // Bonbon bonbGen = new Bonbon(pokGen.getType());
                // dresseur.ajouterBonbon(bonbGen);
                // dresseur.ajouterPokemon(pokGen);
                // }
                // break;

                // default:
                // System.out.println("Vous n'avez pas choisi une option valide ! \n");
                // break;

                // }

                case 2:
                    if (dresseur.getPokemons().size() < 6) {

                        System.out.println("Vous n'avez pas assez de Pokémons ! Vous devez en avoir minimum 6 !\n");

                    } else {

                        System.out.println("Vous avez choisi de combattre un autre dresseur !");
                        System.out.println("Composez votre équipe de 6 pokemons !");

                        dresseur.afficherPokemons();

                        while (dresseur.getPokemonsEquipe().size() < 6) {
                            System.out.println("Quel est l'ID du Pokémon que vous voulez ajouter à votre équipe ? ");

                            try {
                                int id = Integer.parseInt(sc.nextLine().trim());
                                if (id >= 0 && id < dresseur.getPokemons().size()) {
                                    dresseur.ajouterPokemonEquipe(id);
                                } else {
                                    System.out.println("ID invalide. Veuillez entrer un ID correct.");
                                }
                            } catch (NumberFormatException e) {
                                System.out.println("Veuillez entrer un nombre valide.");
                            }

                            // Afficher l'équipe actuelle et demander plus de Pokémon si nécessaire
                            if (dresseur.getPokemonsEquipe().size() < 6) {
                                System.out.println("Votre équipe actuelle contient "
                                        + dresseur.getPokemonsEquipe().size() + " Pokémon(s).");
                                dresseur.afficherPokemonsEquipe();
                            }
                        }

                        System.out.println("\n Voici votre équipe complète : ");
                        dresseur.afficherPokemonsEquipe();

                        try {
                            Socket socket = new Socket("localhost", 2000);
                            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                            oos.writeObject(dresseur);
                            oos.flush();

                            // Écouter les messages du serveur
                            String message;
                            while ((message = br.readLine()) != null) {
                                System.out.println("Message reçu du serveur : " + message);
                                if (message.equals("Le gagnant est : " + dresseur.getPseudo())) {
                                    System.out.println("Vous avez gagné 100 XP ! \n");
                                    dresseur.gagnerXp(dresseur.getXp() + 100);
                                    System.out.println("Vous êtes maintenant niveau " + dresseur.getNiveau() + " ! \n");
                                    System.out.println("appuyez sur entrée pour continuer \n");
                                    sc.nextLine();
                                }
                                if (message.equals("Le combat est terminé !")) {

                                    continue;

                                }
                            }

                            // Fermer les ressources

                            socket.close();
                            br.close();
                            oos.close();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        dresseur.listCombat.clear();
                        System.out.println("Appuyez sur entrée pour continuer \n");
                        sc.nextLine();

                        break;

                    }
                    break;

                case 3:

                    System.out.println("Vous avez choisi de voir vos pokemons !\n");
                    if (dresseur.getPokemons().size() == 0) {
                        System.out.println("Vous n'avez pas de Pokémons ! \n");
                    } else {

                        System.out.println("Voici vos Pokémons :");
                        dresseur.afficherPokemons();

                        System.out.println("Que voulez vous faire : \n");
                        System.out.println("1. Evoluer un Pokémon");
                        System.out.println("2. Améliorer les stats d'un Pokémon");
                        System.out.println("3. Supprimer un Pokémon");
                        System.out.println("4. Retourner au menu \n");

                        int choix2 = sc.nextInt();
                        sc.nextLine(); // Pour consommer la nouvelle ligne après avoir lu l'entier

                        switch (choix2) {
                            case 1:
                                System.out.println("Vous avez choisi d'évoluer un Pokémon ! \n");
                                System.out.println("Quel est l'ID du Pokémon que vous voulez faire évoluer ? \n");

                                int id2 = sc.nextInt();
                                sc.nextLine(); // Pour consommer la nouvelle ligne après avoir lu l'entier

                                if (id2 >= 0 && id2 < dresseur.getPokemons().size()) {
                                    Pokemon pokEvolution = dresseur.getPokemons().get(id2);
                                    pokEvolution.evolution(dresseur);
                                } else {
                                    System.out.println("ID invalide.");
                                }
                                break;
                            case 2:
                                System.out.println("Vous avez choisi d'améliorer les stats d'un Pokémon ! \n");
                                System.out.println("Quel est l'ID du Pokémon que vous voulez améliorer ? \n");

                                int id3 = sc.nextInt();
                                sc.nextLine(); // Pour consommer la nouvelle ligne après avoir lu l'entier

                                if (id3 >= 0 && id3 < dresseur.getPokemons().size()) {
                                    Pokemon pokStats = dresseur.getPokemons().get(id3);
                                    pokStats.ameliorerStats(dresseur);
                                } else {
                                    System.out.println("ID invalide.");
                                }
                                break;

                            case 3:
                                System.out.println("Vous avez choisi de supprimer un Pokémon ! \n");
                                System.out.println("Quel est l'ID du Pokémon que vous voulez supprimer ? \n");

                                int id = sc.nextInt();
                                sc.nextLine(); // Pour consommer la nouvelle ligne après avoir lu l'entier

                                if (id >= 0 && id < dresseur.getPokemons().size()) {
                                    dresseur.supprimerPokemon(id);
                                } else {
                                    System.out.println("ID invalide.");
                                }
                                break;

                            case 4:
                                System.out.println("Vous avez choisi de retourner au menu ! \n");
                                break;

                            default:
                                System.out.println("Vous n'avez pas choisi une option valide ! \n");
                                break;
                        }

                    }

                    break;

                case 4:

                    System.out.println("Vous avez choisi de voir vos bonbons !\n");
                    if (dresseur.bonbons.size() == 0) {

                        System.out.println("Vous n'avez pas de bonbons ! \n");

                    } else {

                        System.out.println("Voici vos bonbons :");
                        dresseur.afficherBonbons();
                        System.out.println("appuyez sur entrée pour continuer \n");
                        sc.nextLine();

                    }

                    break;

                case 5:

                    System.out.println("Vous avez choisi de votre niveau !\n");
                    System.out.println("Niveau : " + dresseur.getNiveau() + "\n");
                    System.out.println("XP : " + dresseur.getXp() + "\n");
                    System.out.println("appuyez sur entrée pour continuer \n");
                    sc.nextLine();

                case 6:

                    System.out.println("Sauvegarde effectuée !");

                    dresseur.save(filePath);
                    break;

                case 7:
                    System.out.println("Vous avez choisi de charger une partie ! \n");

                    System.out.println("quel est le pseudo de votre sauvegarde ?");
                    String pseudoCharge = sc.nextLine();

                    String fileCharge = "src\\sauvegarde\\" + pseudoCharge + ".txt";

                    if (!new File(fileCharge).exists()) {
                        System.out.println("Ce pseudo n'existe pas !");
                        break;
                    }

                    dresseur = Dresseur.charge(fileCharge);
                    pseudo = pseudoCharge;
                    filePath = fileCharge;

                    break;

                case 8:
                    System.out.println("Vous avez choisi de quitter le jeu !\n");
                    System.out.println("Votre partie a été sauvegardée ! \n");
                    dresseur.save(filePath);
                    sc.close(); // Ferme le scanner avant de quitter
                    System.exit(0); // Termine le programme
                    break;

                default:
                    System.out.println("Vous n'avez pas choisi une option valide ! \n");
                    break;
            }
        }
    }

}
