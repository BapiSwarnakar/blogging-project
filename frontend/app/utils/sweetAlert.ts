import Swal from "sweetalert2";
import type { SweetAlertIcon, SweetAlertResult } from "sweetalert2";

interface ConfirmDialogOptions {
  title?: string;
  text?: string;
  icon?: SweetAlertIcon;
  confirmButtonText?: string;
  confirmButtonColor?: string;
  cancelButtonColor?: string;
}

export const showConfirmDialog = async (
  options: ConfirmDialogOptions = {}
): Promise<SweetAlertResult> => {
  const {
    title = "Are you sure?",
    text = "You won't be able to revert this!",
    icon = "warning",
    confirmButtonText = "Yes, delete it!",
    confirmButtonColor = "#2563eb", // blue-600
    cancelButtonColor = "#dc2626", // red-600
  } = options;

  return Swal.fire({
    title,
    text,
    icon,
    showCancelButton: true,
    confirmButtonColor,
    cancelButtonColor,
    confirmButtonText,
    background: "#17212fff",
    color: "#ffffff",
  });
};

export const showSuccessAlert = (title: string, text?: string) => {
  return Swal.fire({
    icon: "success",
    title,
    text,
    background: "#17212fff",
    color: "#ffffff",
    confirmButtonColor: "#2563eb",
  });
};

export const showErrorAlert = (title: string, text?: string) => {
  return Swal.fire({
    icon: "error",
    title,
    text,
    background: "#17212fff",
    color: "#ffffff",
    confirmButtonColor: "#2563eb",
  });
};
