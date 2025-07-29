function toEditMode() {
    // edit-mode: hidden 제거, read-mode: hidden 추가
    document.querySelectorAll('.edit-mode').forEach(element => {
        element.removeAttribute('hidden');
    });
    document.querySelectorAll('.read-mode').forEach(element => {
        element.setAttribute('hidden', '');
    });
    // 역할 체크박스 활성화
    document.querySelectorAll('#user-roles-container input[type="checkbox"]').forEach(input => {
        input.removeAttribute('disabled');
    });
    // 수정취소 버튼 보이기, 수정하기 버튼 숨기기
    document.getElementById('read-mode-btn')?.removeAttribute('hidden');
    document.getElementById('edit-mode-btn')?.setAttribute('hidden', '');
}

function toReadMode() {
    // edit-mode: hidden 추가, read-mode: hidden 제거
    document.querySelectorAll('.edit-mode').forEach(element => {
        element.setAttribute('hidden', '');
    });
    document.querySelectorAll('.read-mode').forEach(element => {
        element.removeAttribute('hidden');
    });
    // 역할 체크박스 비활성화
    document.querySelectorAll('#user-roles-container input[type="checkbox"]').forEach(input => {
        input.setAttribute('disabled', '');
    });
    // 수정취소 버튼 숨기기, 수정하기 버튼 보이기
    document.getElementById('read-mode-btn')?.setAttribute('hidden', '');
    document.getElementById('edit-mode-btn')?.removeAttribute('hidden');
}

function getUserData() {
    return {
        email: getEmail(),
        password: getPassword(),
        roles: getRoles()
    }
}

function getEmail() {
    const email = document.getElementById('user-email-input').value.trim();
    if (email === '') {
        return null;
    }
    return email;
}

function getPassword() {
    const password = document.getElementById('user-password-input').value.trim();
    if (password === '') {
        return null;
    }
    return password;
}

function  getRoles() {
    const roles = [];
    document.querySelectorAll('#roles-container input[type="checkbox"]:checked').forEach(input => {
        roles.push(input.value);
    });
    return roles;
}

async function requestEditUser() {
    const userId = document.getElementById('user-id-input').value.trim();
    const userData = getUserData();
    try {
        const res = await fetch(`/api/users/${userId}`, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(userData)
        });

        await alertToResponse("사용자 수정", res, (body) => {
            window.location.reload();
        });
    } catch (error) {
        console.error("사용자 수정 중 오류 발생:", error);
        alert("사용자 수정 중 오류가 발생했습니다. 다시 시도해주세요.");
    }
}