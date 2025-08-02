// Admin's options management script

const modal = document.getElementById('modal');
let modalMode = 'none';

// Functions of Open and close modal
function openModal(mode, productId = null) {
    modalMode = mode;
    modal.style.display = 'block';
}
function closeModal() {
    modal.style.display = 'none';
}

document.getElementById('infoForm').addEventListener('submit', function(event) {
    event.preventDefault();
    addOption()
});

function addOption() {
    sendOptionData(`${baseUrl}/api/products/${product.id}/options`, 'POST');
}

function sendOptionData(url, method) {
    const formData = new FormData(document.getElementById("infoForm"));
    const data = Object.fromEntries(formData.entries());

    fetch(url, {
        method: method,
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(data)
    }).then((value) => {
        if (value.ok) {
            onSuccess();
        } else {
            value.text().then(value=>{
                onFailure(value);
            })
        }
    });
}

function onSuccess() {
    location.reload();
    closeModal();
}

function onFailure(reason) {
    alert(reason);
}