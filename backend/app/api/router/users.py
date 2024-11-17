import os
from typing import Annotated
import shutil

from fastapi import APIRouter, Depends, HTTPException, status, UploadFile, File
from sqlalchemy.orm import Session

from app import app
from app import deps, schemas, security, settings
from app.database import crud

router = APIRouter()


@router.get("/current", response_model=schemas.users.User)
async def read_users_me(
    current_user: Annotated[schemas.users.User, Depends(deps.get_current_user)],
):
    """
    获取当前用户信息
    """
    return current_user


@router.post("/", response_model=schemas.users.User)
async def create_user(
    user_in: schemas.users.UserCreate, db: Session = Depends(deps.get_db)
):
    """
    创建用户
    """
    user = crud.get_user_by_username(db, username=user_in.username)
    if user:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Username already registered",
        )
    user = crud.create_user(db=db, user=user_in)
    return user


@router.put("/current/password")
async def update_password(
    current_user: Annotated[schemas.users.User, Depends(deps.get_current_user)],
    password_data: schemas.users.UserPasswordUpdate,
    db: Session = Depends(deps.get_db),
):
    """
    更新密码
    """
    user = security.authenticate_user(
        db, username=current_user.username, password=password_data.old_password
    )
    if not user:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Incorrect old password",
        )
    crud.update_user_password(db=db, user=user, new_password=password_data.new_password)
    return {"uid": user.uid}


@router.put("/current/username")
async def update_username(
    current_user: Annotated[schemas.users.User, Depends(deps.get_current_user)],
    username_data: schemas.users.UserUsernameUpdate,
    db: Session = Depends(deps.get_db),
):
    """
    更新用户名
    """
    user = crud.get_user_by_username(db, username=username_data.username)
    if user:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Username already registered",
        )
    updated_user = crud.update_user_username(
        db=db, user=current_user, new_username=username_data.username
    )
    return {"uid": updated_user.uid, "username": updated_user.username}


@router.put("/current/avatar")
async def update_avatar(
    current_user: Annotated[schemas.users.User, Depends(deps.get_current_user)],
    file: UploadFile = File(...),
    db: Session = Depends(deps.get_db),
):
    """
    更新用户头像
    """
    avatar_path = f"{settings.STATIC_DIR}/avatars/{current_user.uid}"
    if not os.path.exists(avatar_path):
        os.makedirs(avatar_path, exist_ok=True)

    # 保存上传的文件
    file_location = f"{avatar_path}/{file.filename}"
    with open(file_location, "wb") as buffer:
        shutil.copyfileobj(file.file, buffer)

    # 更新用户头像路径
    file_path = f"/avatars/{current_user.uid}/{file.filename}"
    updated_user = crud.update_user_avatar(
        db=db, user=current_user, file_path=file_path
    )
    return {
        "uid": updated_user.uid,
        "avatar": app.url_path_for("static", path=file_path),
    }
