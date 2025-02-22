var slideIndex = 1;
showDivs(slideIndex);  // näitab esimest pilti

function plusDivs(n) {
    showDivs(slideIndex += n);  // muudab muutuja slideIndex väärtust vastavalt sellele, kumba nuppu vajutati ja kuvab vastava pildi
}

function showDivs(n) {  // funktsioon peidab kõik ülejäänud pildid ja kontrollib, et indeksid ei läheks piiridest välja
    var i;
    var x = document.getElementsByClassName("mySlides");
    if (n > x.length) {slideIndex = 1} // kui indeks läheb suuremaks kui piltide arv, pannakse muutuja 1-ks ehk kuvatake esimene pilt
    if (n < 1) {slideIndex = x.length} ; // kui indeks läheb negatiivseks pannakse muutuja väärtuseks piltide arv
    for (i = 0; i < x.length; i++) {
        x[i].style.display = "none";
    }
    x[slideIndex-1].style.display = "block";
}

// https://www.w3schools.com/w3css/w3css_slideshow.asp
