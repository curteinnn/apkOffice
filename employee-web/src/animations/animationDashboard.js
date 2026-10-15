import gsap from "gsap";

export function animationDashboard() {
  gsap.fromTo(
    ".card-dashboard",
    {
      opacity: 0,
    },
    { opacity: 1 },
  );
}
