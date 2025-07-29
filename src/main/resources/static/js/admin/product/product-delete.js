async function requestDeleteProduct(element) {
    const id = element.parentNode.querySelector("input").value;
    if (!confirm("정말로 이 상품을 삭제하시겠습니까?")) return;


    try {
        const res = await fetch(`/api/products/${id}`, {
            "method": "DELETE",
            "headers": {
                "Content-Type": "application/json"
            }
        });
        await alertToResponseWithNoData("상품 삭제", res, () => {
            window.location.reload();
            }
        );
    } catch (error) {
        console.error("상품 삭제 중 오류 발생:", error);
        alert("상품 삭제 중 오류가 발생했습니다. 다시 시도해주세요.");
    }
}