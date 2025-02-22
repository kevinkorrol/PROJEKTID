/***********************************
 * Programmeerimine II. LTAT.03.007
 * 2024/2025 kevadsemester
 *
 * Kodutöö nr 1a
 * Teema: Massiivid
 *
 * Autor: Kevin Markus Korrol
 *
 **********************************/


/*ideed:
- arvuta kohe välja suurim ning kasuta seda(elimineerid hashsetid ja suuruse kontrolli, kuid pead rohkem suurimat arvutama)
- tee ümber ruutjuure võtmine(pole (vist) vaja, kui teen euleri algarvukontrolli)
*/
import java.util.HashSet;

public class Kodu1a {
    
    static int algarvuRingideArv(int n) {
        // Otstarve: leian kõik algarvuringid, mille iga liige on väiksem kui sisestatud arv
        // Antud: täisarv n
        // Tulemus: kõik väiksemad erinevad algarvuringid
        HashSet<Integer> vastused = new HashSet<Integer>();//võetud siit: https://www.w3schools.com/java/java_hashset.asp, saab ka vb eemaldada kui juba_olnud tööle saan
        HashSet<Integer> juba_olnud = new HashSet<Integer>();

        if ((n & 1) == 0){
            n = n - 1;
        }

        for (int i = n; i > 10; i -= 2){//Kui sul on arv näiteks 359 999 siis see ei sobi sest 5 on sees. Peale selle kontrolli pole mõtet enam vaadata ka arvu 359 997, sest ka selles on 5. Lahendus on lahutada arvust 20 000

            if (i == 11){//äkki tõsta see loopist välja kõige lõppu ja siis vaadata, et kui arv on väiksem millestki siis lisad lõppu 11. Ei teinud kiiremaks
                vastused.add(11);
                break;
            }

            if (sobivusekontroll(i) && !juba_olnud.contains(i)){
                juba_olnud.add(i);
                //kuskil siin äkki tasub suurim välja arvutada ja siis if-lause suurim == i, et edasi minna
                if (fermattest(i)){
                    int liigutatud_arv = numbri_liigutamine(i);
                    int suurim = i;
                    boolean kas_sobib_algarvuringiks = true;
                    int sisend = i;

                    while (sisend >= 10){//kas saaks ühendada pikkuse leidmise ja selle loopi tehes lic while loopi? Tegin aga see pole kiirem/sama kiire
                        sisend /= 10;

                        if (juba_olnud.contains(liigutatud_arv) || liigutatud_arv % 3 == 0 || liigutatud_arv > n){//vb saab lahti suuruse võrdlusest ja olemasaolu võrdlusest
                            kas_sobib_algarvuringiks = false;
                            break;
                        }

                        if (fermattest(liigutatud_arv)){//äkki panna siia !algarvukontroll(liigutatud_arv) siis kas_sobib_algarvuringiks = false ja breakida ning panna operatsioonid if-lausest välja
                            suurim = Math.max(suurim, liigutatud_arv);
                            juba_olnud.add(liigutatud_arv);//võiks välja tõsta
                            juba_olnud.add(i);
                            liigutatud_arv = numbri_liigutamine(liigutatud_arv);//selle peaks vahele jätma, kui tegu on algarvuringi viimase elemendiga. Proovisin, ei saanud kiiremaks
                        }

                        else{
                            kas_sobib_algarvuringiks = false;
                            break;
                        }
                    }
                    if (kas_sobib_algarvuringiks){//mul oli siin && !vastused.contains(suurim)
                        vastused.add(suurim);
                    }
                }
            }
        }
        return vastused.size();
    }

    public static void main(String[] args) {
        System.out.println(algarvuRingideArv(999999));
    }

    public static int numbri_liigutamine(int sisend){
        // Otstarve: Liigutada kõige parempoolne number kõige vasakule
        // Antud: täisarv sisend, mida hakkame muutma
        // Tulemus: täisarv, mille numbrite järjekorda on veidi muudetud

        //int kymnendnumbrite_arv = (int)(Math.log10(sisend) + 1);//can make it faster, for loop ei teinud asja kiiremaks. Siia saaks teha lic 6 if statementi vastavalt arvu pikkusele = samakiire
        //int jaak = sisend % 10;

        return (astendamine(10, arvupikkus(sisend) - 1) * (sisend % 10)) + (sisend / 10);//enda astendamisega saaks ka kiiremaks: exponentation by squaring
    }


    public static boolean sobivusekontroll(int arv){
        // Otstarve: Kontrollida, kas arvust saab teha algarvuringi. Algarvuringi ei ole võimalik moodustada, kui arvus esineb 0, 2, 4, 5, 6 või 8
        // Antud: täisarv arv
        // Tulemus: tõeväärtus, kas algarvuringi moodustamine on võimalik või mitte
        if (arv % 3 == 0){ //kas siia teha lic mitte et % 3 vaid ristsumma % 3? Ristsumma arvutamine on aeglasem
            return false;
        }

        while (arv > 0){
            int kontrollitav = arv % 10;
            if ((kontrollitav & 1) == 0 || kontrollitav == 5){
                return false;
            }
            arv = arv / 10;
        }
        return true;
    }


    public static int astendamine(int alus, int aste){
        //Otstarve: teha astendamist kiiremaks, kasutades Exponentation by squaring algoritmi: https://simple.wikipedia.org/wiki/Exponentiation_by_squaring#:~:text=Exponentiating%20by%20squaring%20is%20an,binary%20expansion%20of%20the%20exponent.
        //Antud: täisarvud alus ja aste
        //Tulemus: astendatud arv

        int tulemus = 1;
        while (aste > 0) {
            if ((aste & 1) == 1) {
                tulemus *= alus;
            }
            alus *= alus;
            aste >>= 1;
        }
        return tulemus;
    }

    public static int arvupikkus(int sisend){
        //Otstarve: leida antud arvu pikkus võimalikult kiiresti
        //Antud: täisarvud sisendina
        //Tulemus: sisendarvu pikkus

        if (sisend > 100000){ return 6;}
        else if (sisend > 10000){ return 5;}
        else if (sisend > 1000){ return 4;}
        else if (sisend > 100){ return 3;}
        else if (sisend > 10){ return 2;}
        return 0;
    }

    public static int modulaarastendamine(long alus, int aste, int mod){
        //Otstarve: kasutan seda Fermat teoreemi rakenduses
        //Antud: täisarvud alus, aste ja mod
        //Tulemus: jääk

        int tulemus = 1;
        alus = alus % mod;

        while (aste > 0) {
            if ((aste & 1) == 1) {
                tulemus = (int) ((tulemus * alus) % mod);
            }
            alus = (alus * alus) % mod;
            aste >>= 1;
        }
        return tulemus;
    }

    public static boolean fermattest(int arv){
        //Otstarve: Kasutan seda funktsiooni algarvu testimise jaoks
        //Antud: täisarv, mida testin
        //Tulemus: tõeväärtus, kas tegu on (tõenäoliselt) algarvuga või ei

        int aste = (arv - 1);

        int jääk = modulaarastendamine(2, aste, arv);

        return jääk == 1 || jääk == arv - 1;

    }
}
