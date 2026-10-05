(() => {
  const sidebar = document.getElementById('sidebar');
  document.querySelectorAll('[data-sidebar-toggle]').forEach(btn => btn.addEventListener('click', () => sidebar?.classList.toggle('open')));
  document.addEventListener('click', e => {
    if (window.innerWidth <= 820 && sidebar?.classList.contains('open') && !sidebar.contains(e.target) && !e.target.closest('[data-sidebar-toggle]')) sidebar.classList.remove('open');
  });

  const reveal = document.querySelectorAll('.reveal-card');
  if ('IntersectionObserver' in window) {
    const io = new IntersectionObserver(entries => entries.forEach(entry => {
      if (entry.isIntersecting) { entry.target.classList.add('is-visible'); io.unobserve(entry.target); }
    }), {threshold:.07});
    reveal.forEach(el => io.observe(el));
  } else reveal.forEach(el => el.classList.add('is-visible'));

  document.querySelectorAll('[data-auto-dismiss]').forEach(el => setTimeout(() => {
    el.style.opacity='0'; el.style.transform='translateY(-8px)'; setTimeout(() => el.remove(),260);
  }, 4200));

  const COLORS = ['#16A34A','#22C55E','#15803D','#86EFAC','#4ADE80','#166534','#BBF7D0','#F59E0B'];
  const textColor = '#64748B', gridColor = '#E8F0EB';

  function dataFor(canvas){
    const node = document.getElementById(canvas.dataset.source);
    if(!node) return [];
    try { return JSON.parse(node.textContent || '[]'); } catch(e){ return []; }
  }
  function setup(canvas){
    const rect=canvas.getBoundingClientRect(), dpr=Math.min(window.devicePixelRatio||1,2);
    const w=Math.max(260,rect.width), h=Math.max(220,rect.height);
    canvas.width=Math.floor(w*dpr); canvas.height=Math.floor(h*dpr);
    const ctx=canvas.getContext('2d'); ctx.setTransform(dpr,0,0,dpr,0,0); return {ctx,w,h};
  }
  function roundedRect(ctx,x,y,w,h,r){const rr=Math.min(r,w/2,h/2);ctx.beginPath();ctx.moveTo(x+rr,y);ctx.arcTo(x+w,y,x+w,y+h,rr);ctx.arcTo(x+w,y+h,x,y+h,rr);ctx.arcTo(x,y+h,x,y,rr);ctx.arcTo(x,y,x+w,y,rr);ctx.closePath()}
  function empty(ctx,w,h){ctx.fillStyle=textColor;ctx.font='12px system-ui';ctx.textAlign='center';ctx.fillText('No data for the selected filter',w/2,h/2)}
  function label(ctx,text,x,y,align='center'){ctx.fillStyle=textColor;ctx.font='10px system-ui';ctx.textAlign=align;ctx.fillText(String(text).length>16?String(text).slice(0,15)+'…':text,x,y)}
  function grid(ctx,w,h,left=42,bottom=36,top=18,right=16){ctx.strokeStyle=gridColor;ctx.lineWidth=1;for(let i=0;i<5;i++){let y=top+(h-top-bottom)*i/4;ctx.beginPath();ctx.moveTo(left,y);ctx.lineTo(w-right,y);ctx.stroke()}return {left,right,top,bottom,pw:w-left-right,ph:h-top-bottom}}
  function bar(canvas,data){const {ctx,w,h}=setup(canvas);ctx.clearRect(0,0,w,h);if(!data.length)return empty(ctx,w,h);const g=grid(ctx,w,h);const max=Math.max(...data.map(d=>d.value),1)*1.18;const slot=g.pw/data.length,bw=Math.min(44,slot*.58);data.forEach((d,i)=>{const bh=g.ph*d.value/max,x=g.left+i*slot+(slot-bw)/2,y=g.top+g.ph-bh;const grad=ctx.createLinearGradient(0,y,0,y+bh);grad.addColorStop(0,'#22C55E');grad.addColorStop(1,'#15803D');ctx.fillStyle=grad;roundedRect(ctx,x,y,bw,bh,8);ctx.fill();label(ctx,d.label,x+bw/2,h-13);ctx.fillStyle='#0F172A';ctx.font='bold 10px system-ui';ctx.textAlign='center';ctx.fillText(Math.round(d.value),x+bw/2,Math.max(13,y-6))})}
  function lineOrArea(canvas,data,area){const {ctx,w,h}=setup(canvas);ctx.clearRect(0,0,w,h);if(!data.length)return empty(ctx,w,h);const g=grid(ctx,w,h);const vals=data.map(d=>d.value),min=Math.min(...vals),max=Math.max(...vals),range=Math.max(1,max-min);const pts=data.map((d,i)=>({x:g.left+(data.length===1?g.pw/2:i*g.pw/(data.length-1)),y:g.top+g.ph-(d.value-(min-range*.12))/(range*1.24)*g.ph,d}));if(area){const grad=ctx.createLinearGradient(0,g.top,0,g.top+g.ph);grad.addColorStop(0,'rgba(34,197,94,.24)');grad.addColorStop(1,'rgba(34,197,94,.01)');ctx.beginPath();ctx.moveTo(pts[0].x,g.top+g.ph);pts.forEach(p=>ctx.lineTo(p.x,p.y));ctx.lineTo(pts[pts.length-1].x,g.top+g.ph);ctx.closePath();ctx.fillStyle=grad;ctx.fill()}ctx.beginPath();pts.forEach((p,i)=>i?ctx.lineTo(p.x,p.y):ctx.moveTo(p.x,p.y));ctx.strokeStyle='#16A34A';ctx.lineWidth=2.5;ctx.lineJoin='round';ctx.stroke();pts.forEach(p=>{ctx.beginPath();ctx.arc(p.x,p.y,4,0,Math.PI*2);ctx.fillStyle='#fff';ctx.fill();ctx.strokeStyle='#16A34A';ctx.lineWidth=2;ctx.stroke();label(ctx,p.d.label,p.x,h-13)});}
  function donut(canvas,data){const {ctx,w,h}=setup(canvas);ctx.clearRect(0,0,w,h);if(!data.length)return empty(ctx,w,h);const total=data.reduce((a,b)=>a+b.value,0)||1,cx=w*.36,cy=h*.48,r=Math.min(w,h)*.28,inner=r*.58;let start=-Math.PI/2;data.forEach((d,i)=>{const a=d.value/total*Math.PI*2;ctx.beginPath();ctx.arc(cx,cy,r,start,start+a);ctx.arc(cx,cy,inner,start+a,start,true);ctx.closePath();ctx.fillStyle=COLORS[i%COLORS.length];ctx.fill();start+=a});ctx.fillStyle='#0F172A';ctx.font='700 22px system-ui';ctx.textAlign='center';ctx.fillText(Math.round(total),cx,cy+2);ctx.fillStyle=textColor;ctx.font='9px system-ui';ctx.fillText('TOTAL DAYS',cx,cy+18);const lx=w*.7;data.forEach((d,i)=>{const y=34+i*27;ctx.fillStyle=COLORS[i%COLORS.length];roundedRect(ctx,lx,y,9,9,3);ctx.fill();label(ctx,d.label,lx+16,y+8,'left');ctx.fillStyle='#0F172A';ctx.font='700 9px system-ui';ctx.textAlign='right';ctx.fillText(String(Math.round(d.value*10)/10),w-14,y+8)})}
  function render(canvas){const data=dataFor(canvas);switch(canvas.dataset.chart){case'bar':bar(canvas,data);break;case'line':lineOrArea(canvas,data,false);break;case'area':lineOrArea(canvas,data,true);break;case'donut':donut(canvas,data);break;}}
  const charts=[...document.querySelectorAll('.executive-chart')];charts.forEach(render);
  let resizeTimer;window.addEventListener('resize',()=>{clearTimeout(resizeTimer);resizeTimer=setTimeout(()=>charts.forEach(render),140)});
})();
