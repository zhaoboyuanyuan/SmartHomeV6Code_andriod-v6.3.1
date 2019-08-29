var firstStyle = document.createElement("style");
document.documentElement.firstElementChild.appendChild(firstStyle);

// $('body').css('-webkit-text-size-adjust:', '100% !important;');
function firstFunction() {
    var dpr = 1;
    var firstRem = document.documentElement.clientWidth * dpr / 10;
    firstStyle.innerHTML = 'html{font-size:' + firstRem + 'px!important;}';
}

firstFunction();
