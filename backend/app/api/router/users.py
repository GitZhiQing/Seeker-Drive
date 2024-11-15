from typing import Annotated

from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session

from app import deps, schemas, security
from app.database import crud

router = APIRouter()


@router.get("/current")
async def read_users_me(current_user: Annotated[schemas.users.User, Depends(deps.get_current_user)]):
    return current_user


@router.post("/", response_model=schemas.users.User)
async def create_user(user_in: schemas.users.UserCreate, db: Session = Depends(deps.get_db)):
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
        db: Session = Depends(deps.get_db)
):
    user = security.authenticate_user(db, username=current_user.username, password=password_data.old_password)
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
        db: Session = Depends(deps.get_db)
):
    user = crud.get_user_by_username(db, username=username_data.username)
    if user:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Username already registered",
        )
    updated_user = crud.update_user_username(db=db, user=current_user, new_username=username_data.username)
    return {"uid": updated_user.uid, "username": updated_user.username}
