function addWish(id){
  fetch(`/api/wishes/${id}`, { method:"POST", credentials:"include" })
    .then(r=>{ if(r.ok){ alert("추가 완료"); location.reload(); } else alert("추가 실패"); });
}

function deleteWish(id){
  fetch(`/api/wishes/${id}`, { method:"DELETE", credentials:"include" })
    .then(r=>{ if(r.ok){ alert("삭제 완료"); location.reload(); } else alert("삭제 실패"); });
}