const emailInputNode = document.getElementById("user-email-input");
const passwordInputNode = document.getElementById("user-password-input");
const roleInputNodes = document.querySelectorAll('input[name="roles"]');

function getUserFormValues() {
    const email = emailInputNode ? emailInputNode.value : '';
    const password = passwordInputNode ? passwordInputNode.value : '';
    let roles = [];
    roleInputNodes.forEach(input => {
        if (input.checked) {
            roles.push(input.value);
        }
    });
    return { email, password, roles };
}


async function requestCreateUser() {
    const userData = getUserFormValues();
    try {
        const res = await fetch("/api/users", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(userData)
        });

        await alertToResponse("사용자 등록", res, (body) => {
            window.location.href = `/admin/users/${body.id}`;
        });

    }  catch (error) {
        console.error("사용자 등록 중 오류 발생:", error);
        alert("사용자 등록 중 오류가 발생했습니다. 다시 시도해주세요.");
    }
}
