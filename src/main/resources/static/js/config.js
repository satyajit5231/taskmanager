/**
 * config.js — App-wide configuration
 * Author: Satyajeet Sharma
 */

export const API = window.location.hostname === 'localhost'
  ? 'http://localhost:8080'
  : 'https://taskmanager-t0k6.onrender.com';