#!/usr/bin/env python3
"""Run backend CGI scripts locally for frontend development.

This server executes any *.py file under src/backend as CGI so existing
frontend URLs like /tasks-sheet1/.../cgi_wrapper.py continue to work.
"""

from http.server import CGIHTTPRequestHandler, ThreadingHTTPServer, _url_collapse_path
from pathlib import Path
import argparse
import os
from urllib.parse import urlsplit


class LocalCGIHandler(CGIHTTPRequestHandler):
    def end_headers(self) -> None:
        # Add permissive CORS headers for local frontend development.
        self.send_header("Access-Control-Allow-Origin", "*")
        self.send_header("Access-Control-Allow-Methods", "GET, POST, OPTIONS")
        self.send_header("Access-Control-Allow-Headers", "Content-Type, Authorization")
        super().end_headers()

    def do_OPTIONS(self) -> None:
        # Handle CORS preflight before CGI dispatch.
        self.send_response(204)
        self.end_headers()

    # Treat any requested .py file as a CGI script.
    def is_cgi(self) -> bool:
        collapsed_path = _url_collapse_path(self.path)
        path_only = urlsplit(collapsed_path).path
        if not path_only.endswith(".py"):
            return False

        script_path = collapsed_path.lstrip("/")
        if not script_path:
            return False

        self.cgi_info = ("/", script_path)
        return True


def main() -> None:
    parser = argparse.ArgumentParser(description="Local CGI server for propra backend")
    parser.add_argument("--host", default="127.0.0.1", help="Host to bind (default: 127.0.0.1)")
    parser.add_argument("--port", type=int, default=8000, help="Port to bind (default: 8000)")
    args = parser.parse_args()

    backend_root = Path(__file__).resolve().parent
    os.chdir(backend_root)
    current_pythonpath = os.environ.get("PYTHONPATH", "")
    os.environ["PYTHONPATH"] = (
        f"{backend_root}{os.pathsep}{current_pythonpath}"
        if current_pythonpath
        else str(backend_root)
    )

    with ThreadingHTTPServer((args.host, args.port), LocalCGIHandler) as server:
        print(f"Serving backend CGI on http://{args.host}:{args.port}/")
        server.serve_forever()


if __name__ == "__main__":
    main()
