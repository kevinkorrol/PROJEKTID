// Allikas: https://stackoverflow.com/questions/4907843/open-a-url-in-a-new-tab-and-not-a-new-window
// Vajutades nupule, avab uue vahelehe.

function OpenInNewTab(url) {
  window.open(url, '_blank').focus();
}


// Allikas: https://www.freecodecamp.org/news/scroll-animations-with-javascript-intersection-observer-api/ ja ChatGPT
// Ihaste tutvustuse teksti animatsioon, et pikk tekst näeks ilusam ja interaktiivsem välja.
// Kasutame Intersection Observer API, mis jälgib, kas antud element on vaateväljas või mitte.
// Indeks.css failis on määratud, et algselt on läbipaistvus 0. Kuid kui me seda väärtust muudame, siis tekib sujuv animatsioon ühe sekundi jooksul.

window.addEventListener('load', () => {
  const paragraphs = document.querySelectorAll('.sujuv'); // Valib kõik teksti elemendid
  const title = document.querySelector('#algus'); // Animatsioon algab, kui kasutaja vaateväljas on h3 element, mille klass on "algus"

  const options = {
    root: null, // Vaateväli on brauseri enda aken
    rootMargin: '0px',
    threshold: 0.5 // h3 element peab olema 50% ulatuses nähtaval
  };

  const observer = new IntersectionObserver((entries, observer) => {
    entries.forEach(entry => {
      if (entry.isIntersecting) { // Kontrollib, kas element on nähtav
        paragraphs.forEach((para, index) => { // For-tsükkel, mis valib iga paragrahvi
          setTimeout(() => {
            para.style.opacity = 1; // Vallandab animatsiooni
          }, index * 1500); // Viivituse lisamine 1,5 sekundi kaupa
        });
        observer.disconnect(); // h3 elemendi jälgimine peatub, rohkem enam ei jälgita
      }
    });
  }, options);

  observer.observe(title); // Käivitab funktsiooni, mille parameeteriks on h3 element
});
