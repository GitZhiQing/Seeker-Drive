from typing import Annotated

from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session

from app import deps, schemas
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
