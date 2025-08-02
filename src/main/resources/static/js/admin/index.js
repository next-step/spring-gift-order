// 공통 유틸리티 함수들

// fetch 요청에 대한 공통적인 응답 처리 함수
async function alertToResponse(action, response, onSuccess, onError) {
    if (response.ok) {
        alert(`${action}이(가) 성공적으로 완료되었습니다!`);
        if (onSuccess) {
            const result = await response.json();
            onSuccess(result);
        }
    } else {
        await handlingErrorResponse(action, response, onError);
    }
}

async function alertToResponseWithNoData(action, response, onSuccess, onError) {
    if (response.ok) {
        alert(`${action}이(가) 성공적으로 완료되었습니다!`);
        if (onSuccess) {
            onSuccess();
        }
    } else {
        await handlingErrorResponse(action, response, onError);
    }
}

// 에러 응답을 처리하는 함수
async function handlingErrorResponse(action, response, onError) {
    const errorData = await response.json();
    console.error(`${action} 요청 실패:`, errorData);
    if (errorData.validationErrors && errorData.validationErrors.length > 0) {
        const errorMessages = errorData.validationErrors.map(err => `${err.field}: ${err.message}`).join("\n");
        alert(errorMessages);
    } else if (errorData.detail) {
        alert(`${action}에 실패했습니다: ${errorData.detail}`);
    } else {
        alert(`${action} 중 알 수 없는 오류가 발생했습니다.`);
    }
    if (onError) {
        onError(errorData);
    }
}

async function logout() {

}
