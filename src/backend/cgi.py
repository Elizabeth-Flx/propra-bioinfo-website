"""Minimal compatibility shim for the removed stdlib cgi module (Python 3.13+).

This provides only what this project uses: FieldStorage.keys() and
FieldStorage.getvalue() for query/form parameters.
"""

from urllib.parse import parse_qs
import os
import sys
from typing import Dict, List, Optional


class FieldStorage:
    def __init__(self) -> None:
        self._data: Dict[str, List[str]] = {}
        method = os.environ.get("REQUEST_METHOD", "GET").upper()

        if method == "GET":
            query = os.environ.get("QUERY_STRING", "")
            self._data = parse_qs(query, keep_blank_values=True)
            return

        if method == "POST":
            content_type = os.environ.get("CONTENT_TYPE", "")
            content_length = os.environ.get("CONTENT_LENGTH", "")
            try:
                length = int(content_length) if content_length else None
            except ValueError:
                length = None

            raw = sys.stdin.read(length) if length is not None else sys.stdin.read()

            if "application/x-www-form-urlencoded" in content_type:
                self._data = parse_qs(raw, keep_blank_values=True)
            else:
                # JSON and other body types are handled directly in project scripts.
                self._data = {}

    def keys(self):
        return self._data.keys()

    def getvalue(self, key: str, default: Optional[str] = None):
        values = self._data.get(key)
        if not values:
            return default
        if len(values) == 1:
            return values[0]
        return values
