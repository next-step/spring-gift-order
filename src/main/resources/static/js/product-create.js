function handleCancel() {
  window.location.href = getProductListUrl();
}

function handleSubmit(e) {
  e.preventDefault();

  const form = e.target;
  const productData = extractProductData(form);

  if (!validateProductData(productData)) {
    alert("모든 필드를 올바르게 입력해주세요.");
    return;
  }

  fetch(form.action, {
    method: "POST",
    headers: {
      "Content-Type": "application/json"
    },
    body: JSON.stringify(productData)
  })
  .then(async (res) => {
    if (!res.ok) {
      const errorBody = await res.json();
      const message = errorBody?.message || "상품 등록 중 오류가 발생했습니다.";
      throw new Error(message);
    }
    return res.json();
  })
  .then(() => {
    alert("상품이 등록되었습니다.");
    window.location.href = getProductListUrl();
  })
  .catch((error) => {
    console.error("서버 오류:", error);
    alert(error.message || "상품 등록 중 오류가 발생했습니다.");
  });
}



function extractProductData(form) {
  return {
    name: form.name.value.trim(),
    price: Number(form.price.value),
    imageUrl: form.imageUrl.value.trim(),
  };
}

function validateProductData({name, price, imageUrl}) {
  return name !== '' && price > 0 && imageUrl !== '';
}

async function submitProduct(url, data) {
  const response = await fetch(url, {
    method: 'POST',
    headers: {'Content-Type': 'application/json'},
    body: JSON.stringify(data),
  });
  if (!response.ok) {
    throw new Error('상품 등록 실패');
  }
}

function getProductListUrl() {
  return '/admin/products';
}

document.getElementById('cancel-btn').addEventListener('click', handleCancel);
document.getElementById('product-create-form').addEventListener('submit',
    handleSubmit);