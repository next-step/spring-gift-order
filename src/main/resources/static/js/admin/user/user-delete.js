async function requestDeleteUser() {
    if (!confirm("정말로 이 사용자를 삭제하시겠습니까?(삭제된 사용자는 복구할 수 없습니다.)")) {
        return;
    }
    const userId = document.getElementById('user-id-input').value;
    try {
        const res = await fetch(`/api/users/${userId}`, {
            method: "DELETE",
            headers: {
                "Content-Type": "application/json",
                "credentials": "include"
            }
        })
        await alertToResponseWithNoData("사용자 삭제", res, () => {
            window.location.href = "/admin/users";
        });
    } catch (error) {
        console.error("사용자 삭제 중 오류 발생:", error);
        alert("사용자 삭제 중 오류가 발생했습니다. 다시 시도해주세요.");
    }
}