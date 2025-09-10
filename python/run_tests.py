#!/usr/bin/env python3
"""
Test runner for the Supermarket Receipt Kata unit tests.

This script runs all unit tests and provides coverage information.
"""

import subprocess
import sys

def run_tests():
    """Run all unit tests"""
    print("Running unit tests...")
    result = subprocess.run([
        sys.executable, "-m", "pytest", 
        "tests/test_supermarket.py", 
        "-v"
    ], cwd=".")
    
    if result.returncode != 0:
        print("❌ Tests failed!")
        return False
        
    print("✅ All tests passed!")
    return True

def run_coverage():
    """Run tests with coverage report"""
    print("\nRunning tests with coverage...")
    
    # Run tests with coverage
    subprocess.run([
        sys.executable, "-m", "coverage", "run", 
        "-m", "pytest", "tests/test_supermarket.py"
    ], cwd=".")
    
    # Generate coverage report
    subprocess.run([
        sys.executable, "-m", "coverage", "report",
        "catalog.py", "model_objects.py", "receipt.py", 
        "shopping_cart.py", "teller.py", "receipt_printer.py"
    ], cwd=".")

if __name__ == "__main__":
    if run_tests():
        run_coverage()
    else:
        sys.exit(1)