var modal = document.getElementById("myModal");

function offerSelection() {

    modal.style.display = 'block';
}

function closeOffer() {

    modal.style.display = 'none';

}

window.onclick = function (event) {
    if (event.target === modal) {
        modal.style.display = 'none';
    }
}