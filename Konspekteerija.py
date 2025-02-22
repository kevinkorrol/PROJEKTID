from bs4 import BeautifulSoup
from MoodleGet import MoodleAPI #https://pypi.org/project/MoodleGet/ - link teegile
#from os import environ - seda saad kasutada siis, kui ei taha oma tokenit iga kord eraldi kleepida, kuid koodi peab veidi ümber tegema
import tkinter as tk
from tkinter import messagebox
#import json - seda libraryt pole koodi jaoks otseselt tarvis, aga jätsin ta alles, kui tahta midagi kontrollida

def userid():
    """
    Esimese sammuna leiame õpilase UserID, kasutades tudengi turbevõtit.
    Turbevõtme leiab järgmiselt: Moodles paremalt ülevalt eelistused --> turbevõtmed --> Moodle mobile web service
    """
    argumendid = {
        "wstoken": token_moodle,
        "wsfunction": "core_webservice_get_site_info",
        "moodlewsrestformat": "json"
    }
    andmed = moodle.call("core_webservice_get_site_info", params=argumendid)

    return f"{andmed['userid']}"


def testide_id():
    """
    Leiame iga testi ID, selle jaoks läheb vaja Operatsioonisüsteemide kursuse ID-d, mille hardcodesime ise sisse(10892).
    """
    argumendid = {
        "wstoken": token_moodle,
        "wsfunction": "core_course_get_contents",
        "moodlewsrestformat": "json",
        "courseid": 10892
    }
    andmed = moodle.call("core_course_get_contents", params=argumendid)

    tulemus = {}
    for i in range(1, len(andmed[1]["modules"])):
        tulemus[f"Test{i}"] = andmed[1]["modules"][i]["instance"]
    return tulemus


def sooritus_id(testid, kasutaja):
    """
    Nüüd leiame testile vastava soorituse ID, kasutades selleks Testi ja kasutaja ID-d
    """
    tulemus = {}

    for i, j in testid.items():
        argumendid = {
            "wstoken": token_moodle,
            "wsfunction": "mod_quiz_get_user_attempts",
            "moodlewsrestformat": "json",
            "quizid": j,
            "userid": kasutaja
        }
        andmed = moodle.call("mod_quiz_get_user_attempts", params=argumendid)
        
        try:
            tulemus[i] = andmed["attempts"][0]["id"]
        except:
            pass

    return tulemus


def oiged_vastused(sooritused):
    """
    Soorituse ID abil pääseme ligi HTML-le, kust saame BeautifulSoup teeki kasutades leida küsimuse teksti ja õiged vastused, need kirjutame konspektifaili.
    """
    for i, j in sooritused.items():
        kogutest = ""
        testnr = int(i.strip("Test"))
        if testnr not in valitud_numbrid:
            continue
        argumendid = {
            "wstoken": token_moodle,
            "wsfunction": "mod_quiz_get_attempt_review",
            "moodlewsrestformat": "json",
            "attemptid": j
        }
        andmed = moodle.call("mod_quiz_get_attempt_review", params=argumendid)
        try:
            for kusimus in range(len(andmed["questions"])):
                html = andmed["questions"][kusimus]["html"]
                kogutest += f" {html}"
        except:
            continue
        ilussupp(kogutest)
    return ""


def ilussupp(html_string):
    """
    Selles funktsioonis toimub HTML-st küsimuste ja õigete vastuste leidmine ning konspektifaili kirjutamine.
    """
    v = []
    supp = BeautifulSoup(html_string, "html.parser")

    for question, answer in zip(supp.find_all("div", class_="qtext"), supp.find_all("div", class_="rightanswer")):
        vastused = answer.get_text(strip=True).replace("Õige vastus on:", "").replace("Õiged vastused on järgmised:", "").replace("Õige vastus on", "").strip()
        v.append([question.get_text(strip=True), vastused])

    with open("konspekt.txt", "a", encoding="utf-8") as f: 
        for i in v:
            f.write(f"{i[0]}\n")
            f.write(f"  - {i[1]}\n")
            f.write("\n")
        f.write("JÄRGMINE TEST!\n")


valitud_numbrid = [] 
def toggle_number(number, button):
    """Nupu olek"""
     
    if number in valitud_numbrid:
        valitud_numbrid.remove(number)
        button.config(relief="raised")
    else:
        valitud_numbrid.append(number)
        button.config(relief="sunken")


def start_program():
    """
    Konspekteerija käivitaja, kui midagi on puudu, annab veateate
    """
    global token
    token = token_entry.get()

    if not token:
        messagebox.showerror("Viga!", "Palun sisesta token!")
        return

    if not valitud_numbrid:
        messagebox.showerror("Viga!","Palun vali vähemalt üks test!")
        return

    messagebox.showinfo("Õnnestus", f"Konspekteerija käivitatud testidega {valitud_numbrid}")
    raam.after(0, lambda: raam.destroy())


raam = tk.Tk()
raam.title("Konspekteerija")

tk.Label(raam, text="Token:").grid(row=0, column=0, pady=5, padx=5, sticky="e")
token_entry = tk.Entry(raam, width=35)
token_entry.grid(row=0, column=1, columnspan=4, pady=5, padx=5)

nupud = []
for i in range(1, 17):
    """
    Koostab nupud 1-16, saab ka lihtsasti nuppude arvu muuta vastavalt testide arvule.
    """
    nupp = tk.Button(raam, text=str(i), width=10, command=lambda n=i, b=None: toggle_number(n, b))
    nupp.grid(row=(i-1)//4 + 1, column=(i-1)%4, pady=5, padx=5)
    nupp.configure(command=lambda n=i, b=nupp: toggle_number(n, b))
    nupud.append(nupp)

start_button = tk.Button(raam, text="Konspekteeri", command=start_program)
start_button.grid(row=5, column=0, columnspan=4, pady=10)
raam.mainloop()


def main():
    url = "https://moodle.ut.ee/webservice/rest/server.php" #selle lingiga suhtleb kood, mitte näppida!
    
    global token_moodle
    token_moodle = token # <- Kasutaja token, mis tuleb talt eraldi GUI-s küsida. Kui tahad ise koodi testida, pane environ["MOODLE_TOKEN"] asemele enda token 
     
    global moodle
    moodle = MoodleAPI(url, token_moodle)

    kasutaja_id = userid()
    testide_ided = testide_id()
    soorituste_ided = sooritus_id(testide_ided, kasutaja_id)
    oiged_vastused(soorituste_ided)


if __name__ == "__main__":
    main()
