from fastapi import APIRouter
from app.api.router import auth, files, users

router = APIRouter()
router.include_router(auth.router, prefix="/auth", tags=["auth"])
router.include_router(files.router, prefix="/files", tags=["files"])
router.include_router(users.router, prefix="/users", tags=["users"])
