import hashlib
import shutil
from pathlib import Path
from typing import Annotated

from fastapi import APIRouter, Depends, UploadFile, File, HTTPException
from fastapi.responses import FileResponse
from sqlalchemy.orm import Session

from app import deps, schemas, settings
from app.database import crud

router = APIRouter()


def get_drive_path(user_id: int) -> Path:
    return Path(settings.DRIVE_DIR) / str(user_id)


def get_file_path(user_id: int, filename: str) -> Path:
    return get_drive_path(user_id) / filename


def ensure_directory_exists(path: Path):
    if not path.exists():
        path.mkdir(parents=True, exist_ok=True)


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
    drive_path = get_drive_path(current_user.uid)
    ensure_directory_exists(drive_path)

    file_path = get_file_path(current_user.uid, file.filename)

    with open(file_path, "wb") as buffer:
        shutil.copyfileobj(file.file, buffer)

    file_size = file_path.stat().st_size

    with open(file_path, "rb") as buffer:
        file_hash = hashlib.md5(buffer.read()).hexdigest()

    file_data = schemas.files.FileCreate(
        name=file.filename,
        size=file_size,
        type=file.content_type,
        status=0,
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
    file_path = get_file_path(db_file.user_id, db_file.name)

    if not file_path.exists():
        raise HTTPException(status_code=404, detail="文件不存在")

    if db_file.user_id != current_user.uid and db_file.status != 1:
        raise HTTPException(status_code=403, detail="无权下载")

    return FileResponse(
        path=file_path,
        filename=db_file.name,
        headers={"Content-Disposition": f"attachment; filename={db_file.name}"},
    )


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
    if not db_file:
        raise HTTPException(status_code=404, detail="文件不存在")

    file_path = Path(settings.DRIVE_DIR) / str(db_file.user_id) / db_file.name

    if not file_path.exists():
        raise HTTPException(status_code=404, detail="文件不存在")

    if db_file.user_id != current_user.uid:
        raise HTTPException(status_code=403, detail="无权删除")

    try:
        file_path.unlink()
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"文件删除失败: {str(e)}")

    try:
        crud.delete_file_by_fid(db, fid=fid)
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"数据库操作失败: {str(e)}")

    return {"message": "删除成功"}


@router.put("/{fid}/status")
def update_file_status(
    fid: int,
    status: int,
    current_user: Annotated[schemas.users.User, Depends(deps.get_current_user)],
    db: Session = Depends(deps.get_db),
):
    """
    更改文件状态
    """
    db_file = crud.get_file_by_fid(db, fid=fid)
    if db_file.user_id != current_user.uid:
        raise HTTPException(status_code=403, detail="无权更改")

    updated_file = crud.update_file_status_by_fid(db=db, fid=fid, status=status)
    return schemas.files.File.from_orm(updated_file)
