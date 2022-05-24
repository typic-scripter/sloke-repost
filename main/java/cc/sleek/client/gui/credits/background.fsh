#extension GL_OES_standard_derivatives : enable

precision highp float;

#define M_PI 3.14159265359

uniform float time;

float tri(float x){
   return abs(2.0*fract(x*0.25-0.25)-1.0)*2.0-1.0;
}

void main( void )
{
	vec2 position = gl_FragCoord.xy;
	float weight=20.0;
	float fineness=0.98;
	float t=time*10.0;
	float t2=time*1.9;
	position.y+=t;

	float at= -M_PI*26.5650511771/180.0;
	float a=tri((cos(at)*(position.x)-sin(at)*position.y)/weight);

	float bt=M_PI*63.4349488229/180.0;
	float b=tri((cos(bt)*(position.x)-sin(bt)*position.y)/weight);

	float ct= -M_PI*63.4349488229/180.0;
	float c=tri((cos(ct)*(position.x)-sin(ct)*position.y)/weight);

	float dt= M_PI*26.5650511771/180.0;
	float d=tri((cos(dt)*(position.x)-sin(dt)*position.y)/weight);

	float z= 0.0;
	z=max(z, (a>fineness ? -0.25+tri(b+t2+M_PI*1.5): 0.0));
	z=max(z, (b>fineness ? -0.25+tri(a+t2+M_PI*1.): 0.0));
	z=max(z, (c>fineness ? -0.25+tri(d+t2+M_PI*0.5): 0.0));
	z=max(z, (d>fineness ? -0.25+tri(c+t2): 0.0));
	gl_FragColor = vec4(z, z, z, 1.0 );
}