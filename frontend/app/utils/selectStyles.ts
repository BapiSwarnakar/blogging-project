import type { StylesConfig } from "react-select";

export const customSelectStyles: StylesConfig<any, true> = {
  control: (base, state) => ({
    ...base,
    backgroundColor: 'transparent',
    borderColor: state.isFocused ? '#3b82f6' : 'rgba(156, 163, 175, 0.4)',
    borderRadius: '0.5rem',
    padding: '2px',
    boxShadow: state.isFocused ? '0 0 0 2px rgba(59, 130, 246, 0.2)' : 'none',
    '&:hover': {
      borderColor: '#3b82f6',
    }
  }),
  menu: (base) => ({
    ...base,
    backgroundColor: 'white',
    borderRadius: '0.5rem',
    border: '1px solid rgba(156, 163, 175, 0.2)',
    boxShadow: '0 10px 15px -3px rgba(0, 0, 0, 0.1)',
    zIndex: 50
  }),
  option: (base, state) => ({
    ...base,
    backgroundColor: state.isFocused ? '#eff6ff' : 'transparent',
    color: state.isFocused ? '#1e40af' : '#374151',
    '&:active': {
      backgroundColor: '#dbeafe',
    }
  }),
  multiValue: (base) => ({
    ...base,
    backgroundColor: '#eff6ff',
    borderRadius: '0.375rem',
    border: '1px solid #bfdbfe',
  }),
  multiValueLabel: (base) => ({
    ...base,
    color: '#1e40af',
    fontWeight: '500',
  }),
  multiValueRemove: (base) => ({
    ...base,
    color: '#3b82f6',
    '&:hover': {
      backgroundColor: '#dbeafe',
      color: '#1e40af',
    },
  }),
};
