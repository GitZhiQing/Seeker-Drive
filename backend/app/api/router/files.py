import hashlib
import os
import shutil
from typing import Annotated

from fastapi import APIRouter, Depends, UploadFile, File
from fastapi.responses import FileResponse
from sqlalchemy.orm import Session

from app import deps, schemas, settings
from app.database import crud

router = APIRouter()


@router.get("/")
def get_files_list(
        current_user: Annotated[schemas.users.User, Depends(deps.get_current_user)],
        db: Session = Depends(deps.get_db),
        skip: int = 0,
        limit: int = 100,
):
    """
    获取文件列表
    """
    files = crud.get_files_by_uid(db, skip=skip, limit=limit, user_id=current_user.uid)
    return files


@router.post("/")
async def post_file(
        current_user: Annotated[schemas.users.User, Depends(deps.get_current_user)],
        file: UploadFile = File(...),
        db: Session = Depends(deps.get_db),
):
    """
    上传文件
    """
    drive_path = f"{settings.DRIVE_DIR}/{current_user.uid}"

    if not os.path.exists(drive_path):
        os.makedirs(drive_path, exist_ok=True)

    file_path = f"{drive_path}/{file.filename}"

    os.makedirs(os.path.dirname(file_path), exist_ok=True)

    with open(file_path, "wb") as buffer:
        shutil.copyfileobj(file.file, buffer)

    file_size = os.path.getsize(file_path)

    with open(file_path, "rb") as buffer:
        file_hash = hashlib.md5(buffer.read()).hexdigest()

    file_data = schemas.files.FileCreate(
        name=file.filename,
        size=file_size,
        type=file.content_type,
        status=1,
        hash=file_hash,
        user_id=current_user.uid,
    )

    db_file = crud.post_file_by_uid(db=db, file=file_data)

    return schemas.files.File.from_orm(db_file)


@router.get("/{fid}")
def get_file(
        fid: int,
        current_user: Annotated[schemas.users.User, Depends(deps.get_current_user)],
        db: Session = Depends(deps.get_db),
):
    """
    下载文件
    """
    db_file = crud.get_file_by_fid(db, fid=fid)

    drive_path = f"{settings.DRIVE_DIR}/{current_user.uid}"

    if not os.path.exists(drive_path):
        os.makedirs(drive_path, exist_ok=True)

    file_path = f"{drive_path}/{db_file.name}"

    if not os.path.exists(file_path):
        return {"error": "文件不存在"}

    # 鉴权
    if db_file.user_id != current_user.uid:
        return {"error": "无权下载"}
    return FileResponse(path=file_path, filename=db_file.name)


@router.delete("/{fid}")
def delete_file(
        fid: int,
        current_user: Annotated[schemas.users.User, Depends(deps.get_current_user)],
        db: Session = Depends(deps.get_db),
):
    """
    删除文件
    """
    db_file = crud.get_file_by_fid(db, fid=fid)

    drive_path = f"{settings.DRIVE_DIR}/{current_user.uid}"

    if not os.path.exists(drive_path):
        os.makedirs(drive_path, exist_ok=True)

    file_path = f"{drive_path}/{db_file.name}"

    if not os.path.exists(file_path):
        return {"error": "文件不存在"}

    # 鉴权
    if db_file.user_id != current_user.uid:
        return {"error": "无权删除"}

    os.remove(file_path)

    # 删除数据库记录
    crud.delete_file_by_fid(db, fid=fid)

    return {"message": "删除成功"}
