from fastapi import FastAPI
from fastapi.responses import JSONResponse
from fastapi.middleware.cors import CORSMiddleware
from src.routes import alerts_routes
from src.routes import technical_logs_routes

app = FastAPI(title="Modulo Envios API", version="1.0")
app.add_middleware(
    CORSMiddleware,
    allow_origins=[
        "http://localhost:4200",
        "http://127.0.0.1:4200",
        "http://localhost:8080",
        "http://200.137.215.94:8080"
    ],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

app.include_router(alerts_routes.router)
app.include_router(technical_logs_routes.router)

@app.get("/health")
async def health_check():
    return JSONResponse(content={"status": "ok"})
