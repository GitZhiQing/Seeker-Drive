from fastapi import FastAPI, APIRouter
from app.api import api_router

from app.core.config import get_settings

settings = get_settings()

app = FastAPI(
    title=settings.APP_NAME,
    openapi_url=f"{settings.API_STR}/openapi.json",
)


@app.get("/")
def read_root():
    return {"message": "Welcome to Seeker Drive API"}


app.include_router(api_router, prefix=settings.API_STR)
