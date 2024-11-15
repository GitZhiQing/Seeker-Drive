import logging
from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from fastapi.staticfiles import StaticFiles

from app.core.config import get_settings


def configure_logging():
    logging.basicConfig(
        level=logging.INFO,
        format="%(asctime)s - %(name)s - %(levelname)s - %(message)s",
        handlers=[logging.StreamHandler()],
    )
    # Set passlib logger level to ERROR to silence warnings
    # Ref: https://github.com/pyca/bcrypt/issues/684#issuecomment-1858400267
    logging.getLogger("passlib").setLevel(logging.ERROR)


settings = get_settings()

app = FastAPI(
    title=settings.APP_NAME,
    openapi_url=f"{settings.API_STR}/openapi.json",
)

configure_logging()
logging.info("Starting application...")

from app import app_init  # noqa

app_init.db_init()
logging.info("Database initialized.")

# CORS
app.add_middleware(
    CORSMiddleware,  # type: ignore
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)
app.mount("/static", StaticFiles(directory=settings.STATIC_DIR), name="static")


@app.get("/")
def read_root():
    return {"message": "Welcome to Seeker Drive API"}


from app.api import api_router  # noqa

app.include_router(api_router, prefix=settings.API_STR)
logging.info("API router included.")
